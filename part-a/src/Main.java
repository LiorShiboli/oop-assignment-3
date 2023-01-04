import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // get the n, seed and bound from the user
        System.out.println("Choose count of files: ");
        int n = scanner.nextInt();
        System.out.println("Choose seed: ");
        int seed = scanner.nextInt();
        System.out.println("Choose bound: ");
        int bound = scanner.nextInt();

        // create the files
        System.out.println("Creating the " + n + " files.");

        String[] fileNames = LinesCounter.createTextFiles(n, seed, bound);

        System.out.println("The files created.\n");

        // count the lines
        long startTime = System.currentTimeMillis();
        int linesCount = LinesCounter.getNumOfLines(fileNames);
        long endTime = System.currentTimeMillis();

        System.out.println("The " + n + " files have " + linesCount + " lines.");

        System.out.println("getNumOfLines take " + (endTime-startTime) + " [ms] (" + linesCount + " files)");

        startTime = System.currentTimeMillis();
        linesCount = LinesCounter.getNumOfLinesThreads(fileNames);
        endTime = System.currentTimeMillis();

        System.out.println("getNumOfLinesThreads take " + (endTime-startTime) + " [ms] (" + linesCount + " files)");

        startTime = System.currentTimeMillis();
        linesCount = LinesCounter.getNumOfLinesThreadPool(fileNames);
        endTime = System.currentTimeMillis();

        System.out.println("getNumOfLinesThreadPool take " + (endTime-startTime) + " [ms] (" + linesCount + " files)");
    }
}
