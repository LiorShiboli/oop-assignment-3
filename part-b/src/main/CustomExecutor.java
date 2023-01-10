package main;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CustomExecutor {
    public CustomExecutor() {
        // TODO
    }

    public int getCurrentMax() {
        // TODO
    }

    public <T> Future<T> submit(Task<T> task) {
        // TODO
    }

    public <T> Future<T> submit(Callable<T> callable) {
        // TODO
    }

    public <T> Future<T> submit(Callable<T> callable, TaskType taskType) {
        // TODO
    }

    public <T> Future<T> submit(Callable<T> callable, int priority) {
        // TODO
    }

    public void gracefullyTerminate() {
        // TODO
    }

    /**
     * event when task completed
     *
     * @param task that task that complete
     */
    public void doTaskCompleted(Task<?> task) {
        // TODO
    }
}
