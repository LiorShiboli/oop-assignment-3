package main;

import java.util.concurrent.*;

public class CustomExecutor extends ThreadPoolExecutor{
    private int CurrentMax;

    public CustomExecutor() {
        super(Runtime.getRuntime().availableProcessors() / 2, Runtime.getRuntime().availableProcessors() -1,
            300, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>(11, CustomFutureTask.getComparator()));
    }

    public int getCurrentMax() {
        return CurrentMax;
    }

    public <T> Future<T> submit(Task<T> task) {
        // update currentMax
        if (task.getPriority() > this.CurrentMax){
            this.CurrentMax = task.getPriority();
        }

        // submit the task through CustomFutureTask and return the future
        CustomFutureTask<T> customFutureTask = new CustomFutureTask<>(task);
        this.execute(customFutureTask);

        return customFutureTask;
    }
@Override
    public <T> Future<T> submit(Callable<T> callable) {
        return this.submit(Task.createTask(callable));
    }

    public <T> Future<T> submit(Callable<T> callable, TaskType taskType) {
        return this.submit(Task.createTask(callable,taskType));
    }

    public <T> Future<T> submit(Callable<T> callable, int priority) {
        return this.submit(Task.createTask(callable,priority));
    }
 //according to instruction
    public void gracefullyTerminate() {
        this.shutdown();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        //update queue
        if (this.getQueue().isEmpty()){
            this.CurrentMax = 0;
        }
    }
}
