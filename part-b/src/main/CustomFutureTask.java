package main;

import java.util.concurrent.FutureTask;

    public class CustomFutureTask<T> extends FutureTask implements Comparable<CustomFutureTask<T>> {
    public final Task<T> task;


    public CustomFutureTask(Task<T> task) {
        super(task);
        this.task = task;
    }

        @Override
        public int compareTo(CustomFutureTask<T> o) {
            return this.task.compareTo(o.task);
        }
    }
