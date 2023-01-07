package main;

import com.beust.ah.A;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {
        int LAST_NUMBER = 1000000;
        int STEP = 1000;

        ArrayList<PrimesFinder> finders = new ArrayList<>();
        ArrayList<Integer> primes = new ArrayList<>();

        CustomExecutor executor = new CustomExecutor();

        for (int i = 0; i < LAST_NUMBER / STEP; i++) {
            PrimesFinder finder = new PrimesFinder(primes, i * STEP, (i + 1) * STEP, (i / 10) + 1);

            executor.submit(finder.getTask());
            finders.add(finder);
        }

        for (var finder: finders) {
            try {
                int count = finder.getTask().get(100, TimeUnit.MILLISECONDS);
                System.out.println("From " + finder.getStart() + " to " + finder.getEnd() + " find " + count + " primes");
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                System.out.println("Error found!");
            }
        }

        System.out.println("Primes:");
        for (var prime : primes) {
            System.out.println(prime + ", ");
        }
    }
}
