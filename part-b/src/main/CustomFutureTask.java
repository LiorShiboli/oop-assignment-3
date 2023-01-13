package main;

import java.util.Comparator;
import java.util.concurrent.FutureTask;

public class CustomFutureTask<T> extends FutureTask<T> implements Comparable<CustomFutureTask<T>> {
    private static final Comparator<Runnable> COMPARATOR = (task1, task2) -> {
        //check if we can compare two tasks using CustomFutureTask comparator.
        //if not, uses the task compare to default

        if (!(task1 instanceof CustomFutureTask) && !(task2 instanceof CustomFutureTask)) {
            return 0;
        }

        if (!(task2 instanceof CustomFutureTask)) {
            return (((CustomFutureTask<?>) task1).task).compareToDefault();
        }
        if (!(task1 instanceof CustomFutureTask)) {
            return -1 * (((CustomFutureTask<?>) task2).task).compareToDefault();
        }

        return ((CustomFutureTask<?>) task1).task.compareTo(((CustomFutureTask<?>) task2).task);
    };

    public static Comparator<Runnable> getComparator() {
        return COMPARATOR;
    }

    public final Task<T> task;

    public CustomFutureTask(Task<T> task) {
        super(task);
        this.task = task;
    }

    @Override
    public int compareTo(CustomFutureTask<T> task) {
        return this.task.compareTo(task.task);
    }
}
