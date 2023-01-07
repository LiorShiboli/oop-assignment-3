package test;

import main.TaskType;
import main.Task;
import main.CustomExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CustomExecutorTest {
    public static final Logger logger = LoggerFactory.getLogger(CustomExecutorTest.class);

    @Test
    public void build() {
        CustomExecutor executor = new CustomExecutor();

        var task1 = new Task<>(() -> 10 * 10, TaskType.COMPUTATIONAL);

        var task2 = new Task<>(() -> 10 * 10);

        var task3 = Task.createTask(() -> 10 * 10);

        var task4 = Task.createTask(() -> {
            File file = new File("example.txt");

            return file.exists();
        }, TaskType.IO);

        executor.gracefullyTerminate();
    }

    @Test
    public void submitTypesTest() throws ExecutionException, InterruptedException, TimeoutException {
        CustomExecutor executor = new CustomExecutor();

        var counterTask = Task.createTask(() -> {
            int n = 0;
            while (n < 1000) {
                n++;
            }
            return n;
        }, TaskType.COMPUTATIONAL);

        var fileExistsTask = Task.createTask(() -> {
            boolean fileExists = false;
            for (int i = 0; i < 10 && !fileExists; i++) {
                File file = new File("not_exists");
                fileExists = file.exists();
            }
            return fileExists;
        }, TaskType.IO);

        executor.submit(counterTask);
        executor.submit(fileExistsTask);

        assertEquals((int)counterTask.get(), 1000);
        assertEquals(fileExistsTask.get(1, TimeUnit.SECONDS), false);

        assertEquals(executor.getCurrentMax(), 2);
        executor.gracefullyTerminate();
    }

    @Test
    public void multiExecutorsTest() throws ExecutionException, InterruptedException, TimeoutException {
        CustomExecutor executor1 = new CustomExecutor();
        CustomExecutor executor2 = new CustomExecutor();

        List<Task<Integer>> tasks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            tasks.add(executor1.submit(() -> {
                Thread.sleep(25);
                return 1;
            }, TaskType.COMPUTATIONAL));

            tasks.add(executor2.submit(() -> {
                Thread.sleep(50);
                return 2;
            }, TaskType.COMPUTATIONAL));

            tasks.add(executor1.submit(() -> {
                Thread.sleep(25);
                return 3;
            }, TaskType.COMPUTATIONAL));
        }

        for (int i = 0; i < 10; i++) {
            assertEquals((int)tasks.get(i * 3).get(26, TimeUnit.MILLISECONDS), 1);
            assertEquals((int)tasks.get(i * 3 + 1).get( 51, TimeUnit.MILLISECONDS), 2);
            assertEquals((int)tasks.get(i * 3 + 2).get( 26, TimeUnit.MILLISECONDS), 3);
        }

        assertEquals(executor1.getCurrentMax(), 1);
        assertEquals(executor2.getCurrentMax(), 1);

        executor1.gracefullyTerminate();
        executor2.gracefullyTerminate();
    }

    @Test
    public void multiThreadsTest() throws ExecutionException, InterruptedException, TimeoutException {
        CustomExecutor executor1 = new CustomExecutor();

        var mainTask = executor1.submit(() -> {
            CustomExecutor executor2 = new CustomExecutor();

            var taskIO1 = executor2.submit(() -> {
                Thread.sleep(9);
                return "IO-1";
            });

            var taskComp1 = executor2.submit(() -> {
                Thread.sleep(9);
                return "Comp-1";
            });

            var taskIO2 = executor2.submit(() -> {
                Thread.sleep(9);
                return "IO-2";
            });

            var taskComp2 = executor2.submit(() -> {
                Thread.sleep(9);
                return "Comp-2";
            });

            assertThrows(TimeoutException.class, () -> {
                taskIO2.get(10, TimeUnit.MILLISECONDS);
            });

            assertThrows(TimeoutException.class, () -> {
                taskIO1.get(10, TimeUnit.MILLISECONDS);
            });

            assertEquals(taskComp1.get(10, TimeUnit.MILLISECONDS), "Comp-1");
            assertEquals(taskComp2.get(10, TimeUnit.MILLISECONDS), "Comp-2");
            assertEquals(taskIO1.get(10, TimeUnit.MILLISECONDS), "IO-1");
            assertEquals(taskIO2.get(10, TimeUnit.MILLISECONDS), "IO-2");

            assertEquals(executor2.getCurrentMax(), 2);

            executor2.gracefullyTerminate();

            return 0;
        }, TaskType.OTHER);

        assertEquals((int)mainTask.get(300, TimeUnit.MILLISECONDS), 0);

        assertEquals(executor1.getCurrentMax(), 3);

        executor1.gracefullyTerminate();
    }

    @Test
    public void finalTest() {
        CustomExecutor customExecutor = new CustomExecutor();

        var task = Task.createTask(()->{
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
            }
            return sum;
        }, TaskType.COMPUTATIONAL);

        var sumTask = customExecutor.submit(task);

        final int sum;

        try {
            sum = sumTask.get(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        logger.info("Sum of 1 through 10 = " + sum);

        Callable<Double> callable1 = ()-> {
            return 1000 * Math.pow(1.02, 5);
        };

        Callable<String> callable2 = ()-> {
            StringBuilder sb = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            return sb.reverse().toString();
        };

        var priceTask = customExecutor.submit(()-> 1000 * Math.pow(1.02, 5), TaskType.COMPUTATIONAL);

        var reverseTask = customExecutor.submit(callable2, TaskType.IO);
        final Double totalPrice;
        final String reversed;

        try {
            totalPrice = priceTask.get();
            reversed = reverseTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        logger.info("Reversed String = " + reversed);
        logger.info("Total Price = " + totalPrice);
        logger.info("Current maximum priority = " + customExecutor.getCurrentMax());

        assertEquals(customExecutor.getCurrentMax(), 2);

        customExecutor.gracefullyTerminate();
    }
}
