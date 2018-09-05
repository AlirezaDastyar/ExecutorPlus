package space.dastyar.lib.executorplus;

/**
 * Instantiate {@link ExecutorPlus} implementations due to the factory patterns. 
 * There are three main implementation which will be instantiated
 * through static method of this interface.
 * 
 * @see ExecutorPlus
 * @since 0.1 
 * @author Alireza Dastyar
 */
public interface ExecutorPlusFactory {
    
    /**
     * Creates a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue.  At any point, at most
     * {@code nThreads} threads will be active processing tasks.
     * If additional tasks are submitted when all threads are active,
     * they will wait in the queue until a thread is available.
     * The threads in the pool will exist until it is explicitly 
     * {@link ExecutorPlus#shutdown shutdown}.
     *
     * @param nThreads the number of threads in the pool
     * @return the newly created thread pool
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
    public static ExecutorPlus newFixedThreadPool(int nThreads) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException("Thread count can not be zero or less.");
        }
        return new FixedThreadPoolExecutorHandler(nThreads);
    }

    /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to {@code execute} will reuse previously constructed
     * threads if available. If no existing thread is available, a new
     * thread will be created and added to the pool. 
     *
     * @return the newly created thread pool
     */
    public static ExecutorPlus newCachedThreadPool() {
        return new CacheThreadPoolExecutorHandler();
    }
    
     /**
     * Creates an Executor that uses a single worker thread operating
     * off an unbounded queue. (Note however that if this single
     * thread terminates due to a failure during execution prior to
     * shutdown, a new one will take its place if needed to execute
     * subsequent tasks.)  Tasks are guaranteed to execute
     * sequentially, and no more than one task will be active at any
     * given time.
     *
     * @return the newly created single-threaded Executor
     */
    public static ExecutorPlus newSingelThreadPool() {
        return new SingelThreadPoolExecutorHandler();
    }

}
