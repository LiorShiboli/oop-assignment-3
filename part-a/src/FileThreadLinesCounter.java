import java.io.IOException;

class FileThreadLinesCounter implements Runnable {

    // static
    private static long counter = 0;

    public static void resetCounter() {
        counter = 0;
    }

    public static long getCounter() {
        return counter;
    }

    private static synchronized void increaseCounter(long add) {
        counter += add;
    }

    // members
    public String fileName;

    public FileThreadLinesCounter(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        long countOfLines = 0;

        try {
            countOfLines = LinesCounter.getCountOfLinesInFile(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        increaseCounter(countOfLines);
    }
}
