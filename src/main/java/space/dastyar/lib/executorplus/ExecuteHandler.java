package space.dastyar.lib.executorplus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ExecuteHandler implements execution related part of {@link ExecutorPlus}
 * implementation. 
 * 
 * @since 0.1
 * @author Alireza Dastyar
 */
abstract class ExecuteHandler extends ExecutorManager {

    public ExecuteHandler() {
        initWorkers();
    }

    /**
     *
     * @param threadCount indicates count of thread to be created.
     */
    public ExecuteHandler(int threadCount) {
        setThreadCount(threadCount);
        initWorkers();
    }

    @Override
    public void execute(Runnable task) {
        beforeExecution(task);
        getTasks().add(task);
        start();
    }

    @Override
    public Future<?> submit(Runnable task) {
        beforeExecution(task);
        FutureTask f = new FutureTask(task, null);
        getTasks().add(f);
        start();
        return new FutureSimulator(f);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        beforeExecution(task);
        FutureTask<T> f = new FutureTask<T>(task, result);
        getTasks().add(f);
        start();
        return new FutureSimulator(f);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        beforeExecution(task);
        FutureTask<T> f = new FutureTask<T>(task);
        getTasks().add(f);
        start();
        return new FutureSimulator(f);
    }

    @Override
    public <T> T invokAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        Collection<? extends Callable<T>> umTask = Collections.unmodifiableCollection(tasks);
        beforeInvokAnyExecution(umTask);
        List<Future<T>> futures = new ArrayList<>();
        submitTasks(umTask, futures);
        while (true) {
            AtomicInteger failds = new AtomicInteger(0);
            for (Future<T> future : futures) {
                if (future.isDone()) {
                    try {
                        T result = future.get();
                        return result;
                    } catch (ExecutionException e) {
                        throwExecutionExceptionIfNeeded(failds, futures.size(), e);
                    }
                }
            }
            waitFor(100);
        }
    }

    /**
     * If all tasks are failed throws a {@link ExecutionException}; 
     */
    private void throwExecutionExceptionIfNeeded(AtomicInteger failds, int futuresSize, ExecutionException e) throws ExecutionException {
        failds.addAndGet(1);
        if (failds.get() >= futuresSize) {
            throw new ExecutionException("All tasks faild!", e);
        }
    }

    /**
     * Submits tasks from unmodifiable collection and
     * add futures to futures list.
     */
    private <T> void submitTasks(Collection<? extends Callable<T>> umTask, List<Future<T>> futures) {
        for (Callable<T> task : umTask) {
            futures.add(submit(task));
        }
    }

    @Override
    public <T> T invokAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        Collection<? extends Callable<T>> umTask = Collections.unmodifiableCollection(tasks);
        beforeInvokAnyExecution(umTask);
        Date time = new Date(TimeUnit.MILLISECONDS.convert(timeout, unit));
        List<Future<T>> futures = new ArrayList<>();
        submitTasks(umTask, futures);
        while (new Date().before(time)) {
            AtomicInteger failds = new AtomicInteger(0);
            for (Future<T> future : futures) {
                if (future.isDone()) {
                    try {
                        T result = future.get();
                        return result;
                    } catch (ExecutionException e) {
                        throwExecutionExceptionIfNeeded(failds, futures.size(), e);
                    }
                }
            }
            waitFor(100);
        }
        throw new TimeoutException();
    }

    @Override
    public <T> List<Future<T>> invokAll(Collection<? extends Callable<T>> tasks) {
        Collection<? extends Callable<T>> umTask = Collections.unmodifiableCollection(tasks);
        beforeInvokAllExecution(umTask);
        List<Future<T>> futures = new ArrayList<>();
        submitTasks(umTask, futures);
        while (true) {
            boolean isDone = true;
            for (Future<T> future : futures) {
                if (!future.isDone()) {
                    isDone = false;
                }
            }
            if (isDone) {
                break;
            }
        }
        return futures;
    }

    @Override
    public <T> List<Future<T>> invokAll(Collection<? extends Callable<T>> tasks,
             long timeout, TimeUnit unit) {
        Collection<? extends Callable<T>> umTask = Collections.unmodifiableCollection(tasks);
        beforeInvokAllExecution(tasks);
        Date time = new Date(TimeUnit.MILLISECONDS.convert(timeout, unit));
        List<Future<T>> futures = new ArrayList<>();
        submitTasks(umTask, futures);
        while (new Date().before(time)) {
            if (areAllTasksDone(futures)) {
                break;
            }
        }
        return futures;
    }

    /**
     * Checks that all tasks of future list are done or not.
     * @param <T> return type of futures.
     * @param futures list of futures.
     * @return {@code true} if all tasks are done, otherwise {@code false}.
     */
    private <T> boolean areAllTasksDone(List<Future<T>> futures) {
        for (Future<T> future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks validity of task and executor state.
     *
     * @param task
     */
    protected void beforeExecution(Object task) {
        if (task == null) {
            throw new NullPointerException("Task can not be null!");
        }
        if (isShutdown()) {
            throw new RejectedExecutionException("Execution of new tasks is not possible after shutdown!");
        }
    }

    /**
     * validates input of {@link invokAny}.
     * @param <T> return type of futures.
     * @param tasks collections of tasks.
     */
    protected <T> void beforeInvokAnyExecution(Collection<? extends Callable<T>> tasks) {
        if (tasks == null) {
            throw new NullPointerException("Tasks can not be null!");
        }
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("Tasks can not be empty!");
        }
        for (Callable<T> task : tasks) {
            if (task == null) {
                throw new NullPointerException("Task can not be null!");
            }
        }
        if (isShutdown()) {
            throw new RejectedExecutionException("Execution of new tasks is not possible after shutdown!");
        }
    }

    /**
     * validates input of {@link invokAll}.
     * @param <T> return type of futures.
     * @param tasks collections of tasks.
     */
    protected <T> void beforeInvokAllExecution(Collection<? extends Callable<T>> tasks) {
        if (tasks == null) {
            throw new NullPointerException("Tasks can not be null!");
        }
        for (Callable<T> task : tasks) {
            if (task == null) {
                throw new NullPointerException("Task can not be null!");
            }
        }
        if (isShutdown()) {
            throw new RejectedExecutionException("Execution of new tasks is not possible after shutdown!");
        }
    }

    /**
     * Runs after submit of a task.
     * 
     * Note: to be override in subclasses if needed.
     */
    protected void start() {
    }

    /**
     * created and run worker threads.
     */
    protected void initWorkers() {
        for (int i = 0; i < getThreadCount(); i++) {
            createAndStartWorker();
        }
    }

    /**
     * created and run a worker thread.
     */
    protected Worker createAndStartWorker() {
        Worker worker = new Worker(getTasks());
        getThreads().add(worker);
        worker.start();
        return worker;
    }

}
