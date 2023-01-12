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
     * the CustomExecutor of the task
     */
    private CustomExecutor executor;

    /**
     * Create new Task from callable
     *
     * @param callable a callable to run
     */
    public Task(Callable<T> callable) {
        this(callable, TaskType.OTHER);
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
        this.executor = null;
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
        this.executor = null;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Return if the task in an Executor
     * @return true if the task in an Executor
     */
    public boolean inExecutor() {
        return this.executor != null;
    }

    /**
     * Set Executor for the task. Used by the CustomExecutor
     *
     * @param executor a CustomExecutor
     * @throws RuntimeException if already has CustomExecutor
     */
    public void setExecutor(CustomExecutor executor) {
        if (inExecutor()) {
            throw new RuntimeException("Is already in executor");
        }

        this.executor = executor;
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.getPriority(), other.getPriority());
    }

    @Override
    public T call() throws Exception {
        return callable.call();
    }
}
