package main;

import java.util.concurrent.*;

public class CustomExecutor extends ThreadPoolExecutor{
    /**
     * the max priority in the queue
     */
    private int currentMax;

    /**
     * Create default Custom Executor
     */
    public CustomExecutor() {
        super(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() -1,
            300, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>(11, CustomFutureTask.getComparator()));
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        //update queue
        if (this.getQueue().isEmpty()){
            this.currentMax = 0;
        }
    }

    /**
     *
     * @return the max priority in the queue
     */
    public int getCurrentMax() {
        return currentMax;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.submit(Task.createTask(task));
    }

    /**
     * Submit a task
     *
     * @param task the task to submit
     * @param <T> the type of the task's result
     * @return a Future representing pending completion of the task
     */
    public <T> Future<T> submit(Task<T> task) {
        // update currentMax
        if (task.getPriority() > this.currentMax){
            this.currentMax = task.getPriority();
        }

        // submit the task through CustomFutureTask and return the future
        CustomFutureTask<T> customFutureTask = new CustomFutureTask<>(task);
        this.execute(customFutureTask);

        return customFutureTask;
    }

    /**
     * Submit a callable with task type
     *
     * @param task the task to submit
     * @param taskType the task type
     * @param <T> the type of the task's result
     * @return a Future representing pending completion of the task
     */
    public <T> Future<T> submit(Callable<T> task, TaskType taskType) {
        return this.submit(Task.createTask(task, taskType));
    }

    /**
     * Submit callable with custom priority
     *
     * @param task a task to run
     * @param priority the priority of the task
     * @param <T> the type of the task's result
     * @return a Future representing pending completion of the task
     */
    public <T> Future<T> submit(Callable<T> task, int priority) {
        return this.submit(Task.createTask(task, priority));
    }

    /**
     * Wait for all the tasks and terminate the Executor
     *
     * @return true if it gracefully terminated
     */
    public boolean gracefullyTerminate() {
        boolean ret = false;

        this.shutdown();
        try {
            ret = awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) { }

        return ret;
    }
}
