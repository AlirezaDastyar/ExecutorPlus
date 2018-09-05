package space.dastyar.lib.executorplus;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple implementation of {@link Executor} interface 
 * which comes with more information and access on threads
 * state than {@link ExecutorService}.<br>
 * Use {@link ExecutorPlusFactory} for instantiation.
 *
 * @see ExecutorPlusFactory
 * @since 0.1
 * @author Alireza Dastyar
 */
public interface ExecutorPlus extends Executor {

    /**
     * Executes the given command at some time in the future. The command may
     * execute in a new thread, in a pooled thread, or in the calling thread, at
     * the discretion of the Executor implementation.
     *
     * @param task the task to execute.
     *
     * @throws RejectedExecutionException if the task cannot be accepted for
     * execution
     * @throws NullPointerException if {@code command} is null
     *
     * @since 0.01
     */
    @Override
    public void execute(Runnable task);

    /**
     * Submits a Runnable task for execution and returns a Future representing
     * that task. The Future's {@code get} method will return {@code null} upon
     * <em>successful</em> completion.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be scheduled for
     * execution
     * @throws NullPointerException if the task is null
     * @since 0.01
     */
    public Future<?> submit(Runnable task);

    /**
     * Submits a Runnable task for execution and returns a Future representing
     * that task. The Future's {@code get} method will return the given result
     * upon successful completion.
     *
     * @param task the task to submit
     * @param result the result to return
     * @param <T> the type of the result
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be scheduled for
     * execution
     * @throws NullPointerException if the task is null
     */
    public <T> Future<T> submit(Runnable task, T result);

    /**
     * Submits a value-returning task for execution and returns a Future
     * representing the pending results of the task. The Future's {@code get}
     * method will return the task's result upon successful completion.
     *
     * <p>
     * If you would like to immediately block waiting for a task, you can use
     * constructions of the form {@code result = exec.submit(aCallable).get();}
     *
     * @param task the task to submit
     * @param <T> the type of the task's result
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be scheduled for
     * execution
     * @throws NullPointerException if the task is null
     */
    public <T> Future<T> submit(Callable<T> task);

    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do. Upon normal or exceptional return,
     * tasks that have not completed are cancelled.
     * 
     * Note: this method uses an unmodifiable copy of collection, 
     * so consider memory overhead and also changing the collection
     * after its submitted wont effect the queue of tasks.
     *
     * @param tasks the collection of tasks
     * @param <T> the type of the values returned from the tasks
     * @return the result returned by one of the tasks
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if tasks or any element task
     *         subject to execution is {@code null}
     * @throws IllegalArgumentException if tasks is empty
     * @throws ExecutionException if no task successfully completes
     * @throws RejectedExecutionException if tasks cannot be scheduled
     *         for execution
     */
    public <T> T invokAny(Collection<? extends Callable<T>> tasks) 
            throws ExecutionException, InterruptedException; 
    
    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do before the given timeout elapses.
     * Upon normal or exceptional return, tasks that have not
     * completed are cancelled.
     * 
     * Note: this method uses an unmodifiable copy of collection, 
     * so consider memory overhead and also changing the collection
     * after its submitted wont effect the queue of tasks.
     *
     * @param tasks the collection of tasks
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @param <T> the type of the values returned from the tasks
     * @return the result returned by one of the tasks
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if tasks, or unit, or any element
     *         task subject to execution is {@code null}
     * @throws TimeoutException if the given timeout elapses before
     *         any task successfully completes
     * @throws ExecutionException if no task successfully completes
     * @throws RejectedExecutionException if tasks cannot be scheduled
     *         for execution
     */
    public <T> T invokAny(Collection<? extends Callable<T>> tasks,long timeout,TimeUnit unit) 
            throws ExecutionException, InterruptedException, TimeoutException;

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results when all complete.
     * {@link Future#isDone} is {@code true} for each
     * element of the returned list.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception.
     * and this method uses an unmodifiable copy of collection, 
     * so consider memory overhead and also changing the collection
     * after its submitted wont effect the queue of tasks.
     *
     * @param tasks the collection of tasks
     * @param <T> the type of the values returned from the tasks
     * @return a list of Futures representing the tasks, in the same
     *         sequential order as produced by the iterator for the
     *         given task list, each of which has completed
     * @throws NullPointerException if tasks or any of its elements are {@code null}
     * @throws RejectedExecutionException if any task cannot be
     *         scheduled for execution
     */
    public <T> List<Future<T>> invokAll(Collection<? extends Callable<T>> tasks);
    
    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results
     * when all complete or the timeout expires, whichever happens first.
     * {@link Future#isDone} is {@code true} for each
     * element of the returned list.
     * Upon return, tasks that have not completed are cancelled.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception
     * and this method uses an unmodifiable copy of collection, 
     * so consider memory overhead and also changing the collection
     * after its submitted wont effect the queue of tasks.
     *
     *
     * @param tasks the collection of tasks
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @param <T> the type of the values returned from the tasks
     * @return a list of Futures representing the tasks, in the same
     *         sequential order as produced by the iterator for the
     *         given task list. If the operation did not time out,
     *         each task will have completed. If it did time out, some
     *         of these tasks will not have completed.
     * @throws NullPointerException if tasks, any of its elements, or
     *         unit are {@code null}
     * @throws RejectedExecutionException if any task cannot be scheduled
     *         for execution
     */
    public <T> List<Future<T>> invokAll(Collection<? extends Callable<T>> tasks,long timeout,TimeUnit unit);
    
    /**
     * Checks that there is any working thread and if finds a running task
     * within a thread or queued task returns {@code true} otherwise {@code false}. <br>
     * <p>
     * Note: after calling {@link shutdownNow()} always returns {@code false}
     * whether there is any running task or note.
     * 
     * @see shutdownNow()
     */
    public boolean isBusy();

    /**
     * All threads after finish their current tasks wont do any other task until
     * {@link resume()} gets called.
     *
     * @throws IllegalStateException If executor is already paused.
     * @see resume()
     */
    public void pause();

    /**
     * All threads that are on {@link pause()} start to do the tasks.
     *
     * @throws IllegalStateException If executor is not paused.
     * @see pause()
     */
    public void resume();

    /**
     * Clears all queued tasked but threads continue their current task.
     */
    public void clear();

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are
     * executed, but no new tasks will be accepted. Invocation has no additional
     * effect if already shut down.
     *
     * <p>
     * This method does not wait for previously submitted tasks to complete
     * their execution. Use {@link waitToFinish()} to do that.
     *
     * @see waitToFinish()
     */
    public void shutdown();

    /**
     * Attempts to stop all actively executing tasks, halts the processing of
     * waiting tasks, and returns a list of the tasks that were awaiting
     * execution.
     *
     * <p>
     * This method does not wait for actively executing tasks to terminate. Use
     * {@link waitToFinish} to do that.
     *
     * <p>
     * There are no guarantees beyond best-effort attempts to stop processing
     * actively executing tasks. For example, typical implementations will
     * cancel via {@link Thread#interrupt()}, so any task that fails to respond to
     * interrupts may never terminate.
     *
     * @return list of tasks that never commenced execution
     */
    public List<Runnable> shutdownNow();

    /**
     * Returns size of tasks queue which are not processed yet.
     *
     * @return size of tasks queue;
     */
    public int getQueueSize();

    /**
     * Returns count of created thread. this method can be used on cached thread
     * pool scenario which thread count is not fixed.
     *
     * @return size of tasks queue;
     */
    public int getThreadCount();

    /**
     * Blocks until all tasks have completed and every 2100ms checks threads
     * state, also you can implement your own method using {@link isBusy()}.<br>
     * Notes that calling {@link shutdown()} or {@link shutdownNow()} is not required.
     * 
     * @see isBusy()
     */
    public void waitToFinish();
}
