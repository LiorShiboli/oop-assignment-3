package main;

import java.util.ArrayList;

/**
 * Task that find primes in range
 */
public class PrimesFinder {

    /**
     * Check if number is prime
     *
     * @param n the number
     * @return is prime
     */
    private static boolean isPrime(int n) {
        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                return false;
            }
        }

        return n > 1;
    }

    private final int start;
    private final int end;

    private final Task<Integer> task;

    /**
     * Create finder
     *
     * @param primes array of save the primes
     * @param start range from
     * @param end range to
     * @param priority the priority of the task
     */
    public PrimesFinder( ArrayList<Integer> primes, int start, int end, int priority) {
        this.start = start;
        this.end = end;

        this.task = new Task<>(() -> {
            System.out.println("Start finder: From " + start + " to " + end);

            int counter = 0;
            for (int i = start; i < end; i++) {
                if (isPrime(i)) {
                    primes.add(i);
                    counter++;
                }
            }

            return counter;

        }, priority);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Task<Integer> getTask() {
        return task;
    }
}
