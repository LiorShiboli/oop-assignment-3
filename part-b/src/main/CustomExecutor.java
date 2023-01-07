package main;

import java.util.concurrent.Callable;

public class CustomExecutor {
    public CustomExecutor() {

    }

    public int getCurrentMax() {
        return 0;
    }

    public <T> Task<T> submit(Task<T> task) {
        return task;
    }

    public <T> Task<T> submit(Callable<T> callable) {
        return null;
    }

    public <T> Task<T> submit(Callable<T> callable, TaskType taskType) {
        return null;
    }

    public <T> Task<T> submit(Callable<T> callable, int priority) {
        return null;
    }

    public void gracefullyTerminate() {

    }
}
