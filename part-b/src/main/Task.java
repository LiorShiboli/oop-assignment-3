package main;

import java.util.concurrent.*;

public class Task<T> implements Callable<T>, Comparable<Task<?>> {
    /**
     * Create new Task from callable
     *
     * @param callable a callable to run
     * @return the created task
     * @param <T> type of the result of the task
     */
    static public <T> Task<T> createTask(Callable<T> callable) {
        return new Task<>(callable);
    }

    /**
     * Create new Task with TaskType from callable
     *
     * @param callable a callable to run
     * @param taskType the task type
     * @return the created task
     * @param <T> type of the result of the task
     */
    static public <T> Task<T> createTask(Callable<T> callable, TaskType taskType) {
        return new Task<>(callable, taskType);
    }
    static public <T> Task<T> createTask(Callable<T> callable, int priority) {
        return new Task<>(callable, priority);
    }

    /**
     * A callable to compute
     */
    private final Callable<T> callable;

    /**
     * The priority of the task
     */
    private final int priority;


    /**
     * Create new Task from callable
     *
     * @param callable a callable to run
     */
    public Task(Callable<T> callable) {
        this(callable, TaskType.def);
    }

    /**
     * Create new Task with TaskType from callable
     *
     * @param callable a callable to run
     * @param taskType the task type
     */
    public Task(Callable<T> callable, TaskType taskType) {
        this.callable = callable;
        this.priority = taskType.getPriority();
    }

    /**
     * Create new Task from callable with custom priority
     *
     * @param callable a callable to run
     * @param priority the custom priority
     */
    public Task(Callable<T> callable, int priority) {
        this.callable = callable;
        this.priority = priority;
    }

    public static int CompareToDefault(Task task) {
        return Integer.compare(task.getPriority(), TaskType.def.getPriority());
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Return if the task in an Executor
     * @return true if the task in an Executor
     */


    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.getPriority(), other.getPriority());
    }

    @Override
    public T call() throws Exception {
        return callable.call();
    }
}
