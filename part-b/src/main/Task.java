package main;

import java.util.concurrent.*;

public class Task<T> {
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

    /**
     * A callable to run432222222
     */
    Callable<T> callable;

    /**
     * The task type
     */
    private final int priority;

    /**
     * future from the Executor
     */
    Future<T> future;

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
        this.future = null;
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
        this.future = null;
    }

    public Callable<T> getCallable() {
        return callable;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Return if the task in an Executor
     * @return true if the task in an Executor
     */
    public boolean inExecutor() {
        return this.future != null;
    }



    /**
     * Returns {@code true} if this task completed.
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.
     *
     * @see java.util.concurrent.Future#isDone()
     * @return {@code true} if this task completed
     */
    public boolean isDone() {
        if (this.future == null) {
            return false;
        }

        return this.future.isDone();
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     * @throws RuntimeException if the task not in an Executor
     * @see java.util.concurrent.Future#get()
     */
    public T get() throws InterruptedException, ExecutionException, RuntimeException {
        if (!inExecutor()) {
            throw new RuntimeException("Task not in an Executor!");
        }

        return this.future.get();
    }

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     * @throws TimeoutException if the wait timed out
     * @throws RuntimeException if the task not in an Executor
     * @see java.util.concurrent.Future#get(long, TimeUnit)
     */
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException, RuntimeException {
        if (inExecutor()) {
            throw new RuntimeException("Task already in a Executor!");
        }

        return this.future.get(timeout, unit);
    }

    /**
     * init a future from Executor, used by the {@link CustomExecutor}
     *
     * @param future the future
     * @throws RuntimeException if the task not in an Executor
     */
    public void setFuture(Future<T> future) {
        if (inExecutor()) {
            throw new RuntimeException("Task already in a Executor!");
        }

        this.future = future;
    }
}
