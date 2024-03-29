import java.io.*;
import java.nio.file.Files;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class that provides 3 ways to
 * count number of lines in number of files
 */
public class LinesCounter {
    private static final String DIST_PATH = "dist";
    private static final String FILES_PREFIX = "file_";

    private static final int MIN_LINE_LENGTH = 10;
    private static final int MAX_LINE_LENGTH = 100;

    /**
     * Delete if exists and create the folder for the files
     */
    private static void recreateDist() {
        File dist = new File(DIST_PATH);
        if (dist.exists()) {
            File[] files = dist.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        } else {
            dist.mkdir();
        }
    }

    /**
     * Create n files
     *
     * @param n the count of the files
     * @param seed for random
     * @param bound max lines in single file
     * @return list of the names (path) of the files
     */
    public static String[] createTextFiles(int n, int seed, int bound) {
        Random random = new Random(seed);

        recreateDist();

        String[] fileNames = new String[n];

        try {
            // create the first file
            fileNames[0] = DIST_PATH + "/" + FILES_PREFIX + (1);
            FileWriter fileWriter = new FileWriter(fileNames[0]);

            int linesCount = random.nextInt(bound);

            for (int j = 0; j < linesCount; j++) {
                int lineLength = random.nextInt(MAX_LINE_LENGTH - MIN_LINE_LENGTH + 1) + MIN_LINE_LENGTH;
                for (int k = 0; k < lineLength; k++) {
                    fileWriter.append("*");
                }
                fileWriter.append("\n");
            }

            fileWriter.close();

            File file = new File(fileNames[0]);

            for (int i = 1; i < fileNames.length; i++) {
                fileNames[i] = DIST_PATH + "/" + FILES_PREFIX + (i + 1);

                Files.copy(file.toPath(), new File(fileNames[i]).toPath());
            }

        } catch (IOException e) {
            return null;
        }

        return fileNames;
    }

    /**
     * Counter the count of lines in single file
     *
     * @param fileName file path
     * @return count lines
     */
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

    /**
     * Counter the count of lines in files
     *
     * @param fileNames list of files
     * @return count lines
     */
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

    /**
     * Counter the count of lines in files with Threads
     *
     * @param fileNames list of files
     * @return count lines
     */
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

    /**
     * Counter the count of lines in files with ThreadPoolExecutor
     *
     * @param fileNames list of files
     * @return count lines
     */
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

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) { }

        return counter.get();
    }
}
