package main;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {
        int LAST_NUMBER = 1000000;
        int STEP = 10000;

        System.out.println("Init the finders...");

        ArrayList<PrimesFinder> finders = new ArrayList<>();
        ArrayList<Future<Integer>> futures = new ArrayList<>();
        ArrayList<Integer> primes = new ArrayList<>();

        CustomExecutor executor = new CustomExecutor();

        for (int i = 0; i < LAST_NUMBER / STEP; i++) {
            PrimesFinder finder = new PrimesFinder(primes, i * STEP, (i + 1) * STEP, (i % 10) + 1);

            futures.add(executor.submit(finder.getTask()));
            finders.add(finder);
        }

        executor.gracefullyTerminate();

        int counter = 0;

        for (int i = 0; i < finders.size(); i++) {
            try {
                int count = futures.get(i).get(100000, TimeUnit.MILLISECONDS);
                System.out.println("From " + finders.get(i).getStart() + " to " + finders.get(i).getEnd() + " found " + count + " primes");
                counter += count;
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.fillInStackTrace();
            }
        }

        System.out.println(counter + " primes found:");
        for (var prime : primes) {
            System.out.print(prime + ", ");
        }
    }
}
