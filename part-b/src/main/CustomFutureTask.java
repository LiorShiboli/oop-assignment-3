package main;

import java.util.concurrent.FutureTask;

    public class CustomFutureTask<T> implements Runnable {
    public final Task<T> task;
    public final FutureTask<T> futureTask;

    public CustomFutureTask(Task<T> task, FutureTask<T> futureTask) {
        this.task = task;
        this.futureTask = futureTask;
    }

    @Override
    public void run() {
        this.futureTask.run();
    }
}
