package main;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CustomExecutor {
    public CustomExecutor() {

    }

    public int getCurrentMax() {
        return 0;
    }

    public <T> Future<T> submit(Task<T> task) {
        return null;
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return null;
    }

    public <T> Future<T> submit(Callable<T> callable, TaskType taskType) {
        return null;
    }

    public <T> Future<T> submit(Callable<T> callable, int priority) {
        return null;
    }

    public void gracefullyTerminate() {
        // TODO
    }

    public <T> void doTaskCompleted(Task<T> task) {
        // TODO: update max priority
    }
}
