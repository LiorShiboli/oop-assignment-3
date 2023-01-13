package main;

import java.util.Comparator;
import java.util.concurrent.FutureTask;

    public class CustomFutureTask<T> extends FutureTask implements Comparable<CustomFutureTask<T>> {
    public final Task<T> task;


    public CustomFutureTask(Task<T> task) {
        super(task);
        this.task = task;
    }

        public static Comparator<Runnable> COMP = new Comparator<Runnable>(){

            @Override
            public int compare(Runnable task1, Runnable task2) {
                //check if we can compare two tasks using customFuturetask comparator.
                //if not, uses the task compare to default
                if (!(task1 instanceof CustomFutureTask)&&!(task2 instanceof CustomFutureTask)){
                    return 0;
                }
                if ((task1 instanceof CustomFutureTask)){
                    return Task.CompareToDefault(((CustomFutureTask) task1).task);
                }
                if ((task2 instanceof CustomFutureTask)){
                    return -1*Task.CompareToDefault(((CustomFutureTask) task2).task);
                }
                return ((CustomFutureTask) task1).compareTo((CustomFutureTask) task2);

            }
        };

        @Override
        public int compareTo(CustomFutureTask<T>fTask ) {
            return this.task.compareTo(fTask.task);
        }
    }
