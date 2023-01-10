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
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

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

        assertEquals(0, executor.getCurrentMax());

        var counterFuture = executor.submit(counterTask);
        var fileExistsFuture = executor.submit(fileExistsTask);

        assertEquals(1000, (int)counterFuture.get(1, TimeUnit.SECONDS));
        assertEquals(false, fileExistsFuture.get(1, TimeUnit.SECONDS));

        assertEquals(0, executor.getCurrentMax());
        executor.gracefullyTerminate();
    }

    @Test
    public void multiExecutorsTest() throws ExecutionException, InterruptedException {
        final int N = 1000;

        CustomExecutor executor1 = new CustomExecutor();
        CustomExecutor executor2 = new CustomExecutor();

        List<Future<Integer>> tasks = new ArrayList<>();

        for (int i = 0; i < N; i++) {
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

        assertEquals(1, executor1.getCurrentMax());
        assertEquals(1, executor2.getCurrentMax());

        for (int i = 0; i < N; i++) {
            assertEquals((int)tasks.get(i * 3).get(), 1);
            assertEquals((int)tasks.get(i * 3 + 1).get(), 2);
            assertEquals((int)tasks.get(i * 3 + 2).get(), 3);
        }

        assertEquals(0, executor1.getCurrentMax());
        assertEquals(0, executor2.getCurrentMax());

        executor1.gracefullyTerminate();
        executor2.gracefullyTerminate();
    }

    @Test
    public void multiThreadsTest() throws ExecutionException, InterruptedException, TimeoutException {
        CustomExecutor executor1 = new CustomExecutor();

        var mainTask = executor1.submit(() -> {
            CustomExecutor executor2 = new CustomExecutor();

            var taskIO1 = executor2.submit(() -> {
                Thread.sleep(90);
                return "IO-1";
            }, TaskType.IO);

            var taskComp1 = executor2.submit(() -> {
                Thread.sleep(90);
                return "Comp-1";
            }, TaskType.COMPUTATIONAL);

            var taskIO2 = executor2.submit(() -> {
                Thread.sleep(90);
                return "IO-2";
            }, TaskType.IO);

            var taskComp2 = executor2.submit(() -> {
                Thread.sleep(90);
                return "Comp-2";
            }, TaskType.COMPUTATIONAL);

            assertEquals(1, executor2.getCurrentMax());

            assertEquals("Comp-1", taskComp1.get(100, TimeUnit.MILLISECONDS));
            assertEquals("Comp-2", taskComp2.get(100, TimeUnit.MILLISECONDS));
            assertEquals("IO-1", taskIO1.get(100, TimeUnit.MILLISECONDS));
            assertEquals("IO-2", taskIO2.get(100, TimeUnit.MILLISECONDS));

            assertEquals(0, executor2.getCurrentMax());

            executor2.gracefullyTerminate();

            return 0;
        }, TaskType.OTHER);

        assertEquals(0, (int)mainTask.get(1, TimeUnit.SECONDS));

        assertEquals(0, executor1.getCurrentMax());

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

        int sum;

        try {
            sum = sumTask.get(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        logger.info("Sum of 1 through 10 = " + sum);

        Callable<Double> callable1 = () -> 1000 * Math.pow(1.02, 5);

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

        assertEquals(0, customExecutor.getCurrentMax());

        customExecutor.gracefullyTerminate();
    }
}
