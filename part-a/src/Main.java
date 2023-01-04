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

        System.out.println("The files created.");
    }
}
