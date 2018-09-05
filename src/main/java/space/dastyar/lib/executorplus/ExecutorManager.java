package space.dastyar.lib.executorplus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ExecutorManager implements managing threads state and life-cycle
 * of {@link ExecutorPlus} implementation. 
 * 
 * @since 0.1
 * @author Alireza Dastyar
 */
abstract class ExecutorManager implements ExecutorPlus{

    /**
     * List of threads that are created to do the tasks.
     */
    private List<Worker> threads=new ArrayList<>();
    
    /**
     * Queue of tasks that are to be executed.
     */
    private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    
    /**
     * Count of threads that are created or to create.
     */
    private int threadCount;
    
    /**
     * Indicates running state of threads within executors list.
     * 
     * @see Worker#waited 
     */
    private boolean pause=false;
    
    /**
     * Indicates state of executors.
     * 
     * @see shutdown() 
     */
    private boolean shutdown;
    
    /**
     * Indicates state of executors and threads.
     * 
     * @see shutdownNow() 
     */
    private boolean shutdownNow;


    @Override
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * @param threadCount 
     * @see threadCount
     */
    void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * 
     * @return queued tasks
     * @see tasks
     */
    BlockingQueue<Runnable> getTasks() {
        return tasks;
    }

    @Override
    public boolean isBusy()  {
        if (shutdownNow) {
            return false;
        }
        boolean busy = threads.stream().anyMatch(x -> x.isBusy());
        busy = busy || tasks.size()>0;
        return busy;
    }

    /**
     * waits for duration.
     * @param duration 
     */
    protected void waitFor(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    /**
     * returns created threads.
     * 
     * @return {@link threads} 
     */
    public List<Worker> getThreads() {
        return threads;
    }

    @Override
    public int getQueueSize() {
        return tasks.size();
    }

    @Override
    public void pause() {
        if (pause) {
            throw new IllegalStateException("Executor is already pause.");
        }
        threads.forEach((thread)
                -> thread.setWaited(true)
        );
        pause=true;
    }

    @Override
    public void resume() {
        if (!pause) {
            throw new IllegalStateException("Executor is not pause.");
        }
        threads.forEach((thread)
                -> thread.setWaited(false)
        );
        pause=false;
    }

    @Override
    public void clear() {
        tasks.clear();
    }

    @Override
    public void shutdown() {
        shutdown = true;
        threads.forEach((thread)
                -> thread.shutdown()
        );
        threads.clear();
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown = true;
        shutdownNow = true;
        threads.forEach((thread)
                -> thread.kill()
        );
        threads.clear();
        return new ArrayList<>(tasks);
    }

    @Override
    public void waitToFinish() {
        do {
            waitFor(2100);
        } while (this.isBusy());
    }

    protected void setThreads(List<Worker> threads) {
        this.threads = threads;
    }

    protected boolean isShutdown() {
        return shutdown;
    }

}
