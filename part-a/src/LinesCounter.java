import java.io.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class LinesCounter {
    private static final String DIST_PATH = "dist";
    private static final String FILES_PREFIX = "file_";

    private static final int MIN_LINE_LENGTH = 10;
    private static final int MAX_LINE_LENGTH = 100;


    private static void recreateDist() {
        File dist = new File(DIST_PATH);
        if (dist.exists()) {
            File[] files = dist.listFiles();
            if (files != null)
            {
                for (File file: files) {
                    file.delete();
                }
            }
        } else {
            dist.mkdir();
        }
    }

    public static String[] createTextFiles(int n, int seed, int bound) {
        Random random = new Random(seed);

        recreateDist();

        String[] fileNames = new String[n];

        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = DIST_PATH + "/" + FILES_PREFIX + (i + 1);

            // create the file
            try {
                FileWriter file = new FileWriter(fileNames[i]);;

                int linesCount = random.nextInt(bound);

                for (int j = 0; j < linesCount; j++) {
                    int lineLength = random.nextInt(MAX_LINE_LENGTH - MIN_LINE_LENGTH + 1) + MIN_LINE_LENGTH;
                    for (int k = 0; k < lineLength; k++) {
                        file.append("*");
                    }
                    file.append("\n");
                }

                file.close();
            } catch (IOException e) {
                return null;
            }
        }

        return fileNames;
    }

    public static long getCountOfLinesInFile(String fileName) throws IOException {
        long count = 0;

        FileInputStream stream = new FileInputStream(fileName);

        byte[] chunkBuffer = new byte[8192];
        int chunkSize;

        do {
            chunkSize = stream.read(chunkBuffer);

            for (int i = 0; i < chunkSize; i++) {
                if (chunkBuffer[i] == '\n') {
                    count++;
                }
            }
        } while (chunkSize > 0);

        stream.close();

        return count;
    }

    public static long getNumOfLines(String[] fileNames) {

        long counter = 0;

        for (String fileName: fileNames) {
            try {
                counter += getCountOfLinesInFile(fileName);
            } catch (IOException e) {
                return -1;
            }
        }

        return counter;
    }

    public static long getNumOfLinesThreads(String[] fileNames) {

        Thread[] threads = new Thread[fileNames.length];

        FileThreadLinesCounter.resetCounter();

        for (int i = 0; i < fileNames.length; i++) {
            threads[i] = new Thread(new FileThreadLinesCounter(fileNames[i]));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                return -1;
            }
        }

        return FileThreadLinesCounter.getCounter();
    }

    public static long getNumOfLinesThreadPool(String[] fileNames) {

        AtomicLong counter = new AtomicLong(0);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(fileNames.length);

        for (String fileName: fileNames) {
            executor.submit(() -> {
                long linesInFile = 0;
                try {
                    linesInFile = getCountOfLinesInFile(fileName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                counter.getAndAdd(linesInFile);
            });
        }

        while (executor.getActiveCount() > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return counter.get();
    }
}
