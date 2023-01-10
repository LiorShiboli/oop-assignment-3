import java.io.IOException;

/**
 * Class for counting lines in files
 */
class FileThreadLinesCounter implements Runnable {

    // static
    /**
     * Main counter of all the threads
     */
    private static long counter = 0;

    public static void resetCounter() {
        counter = 0;
    }

    public static long getCounter() {
        return counter;
    }

    /**
     * increase the counter
     *
     * @param add count
     */
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
