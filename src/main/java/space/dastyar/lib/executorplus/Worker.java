package space.dastyar.lib.executorplus;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Worker is a customized thread implementation
 * to present more information about internal state of
 * a thread and more suitable for {@link ExecutorPlus}  
 * implementation.
 * 
 * @since 0.1
 * @author Alireza Dastyar
 */
 class Worker extends Thread {

    /**
     * Indicates that is thread executing any task at current time.
     */
    private volatile boolean busy = false;

    /**
     * Indicates life state of thread.
     * @see shutdown()
     */
    private volatile boolean dead = false;
   
    /**
     *  Indicates that thread has finished/stopped working.
     */
    private volatile boolean done = false;

    /**
     * Indicates running state of thread. Setting this variable to {@code true}
     * stop thread from taking(from {@code tasks} queue) or starting a new task.
     */
    private volatile boolean waited = false;

    /**
     * This variable used to efficiently block the thread when it's
     * {@code pause}
     */
    private volatile Semaphore waitLock = new Semaphore(0);

    /**
     * Queue of tasks that are to be done by a thread or group.
     */
    private volatile BlockingQueue<Runnable> tasks;
    
    /**
     * Current task of thread to be done.
     */
    private Runnable currentTask;

    /**
     * @param tasks the queue of task that this thread take task from.
     */
    Worker(BlockingQueue<Runnable> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        while (tasks!=null && (!isDead() || tasks.size() > 0)) {
            boolean brk=beforStart();
            // break if any exception is thrown.
            if (brk) {
                break;
            }
            currentTask.run();
            affterFinish();
        }
        done=true;
    }

    /**
     * Blocks if {@link waited} is {@code ture} or the tasks queue is empty, and
     * changes state of {@link busy}.
     */
    private boolean beforStart() {
        try {
            waitIfNeeded();
            currentTask = tasks.take();
            waitIfNeeded();
            busy = true;
        } catch (Exception ex) {
            return true;
        }
        return false;
    }

    /**
     * Changes busy to {@code false} after finishing a task.
     */
    private void affterFinish() {
        busy = false;
    }

    /**
     * Waits (blocks) if needed :) using {@link waitLock}.
     */
    private void waitIfNeeded() {
        try {
            if (waited) {
                waitLock.acquire();
            }
        } catch (InterruptedException ex) {
            this.interrupt();
        }
    }

     /**
     * 
     * @return {@link busy}
     * 
     * @see busy
     */
    boolean isBusy() {
        return busy;
    }

     /**
     * 
     * @see busy
     */
    void setBusy(boolean busy) {
        this.busy = busy;
    }
    
    /**
     * 
     * @see waited
     */
    boolean isWaited() {
        return waited;
    }
    
    /**
     * 
     * @see done
     */
    boolean isDone(){
        return done;
    }
    
    /**
     * 
     * @return {@link waited}
     * 
     * @see waited
     */
    void setWaited(boolean waited) {
        this.waited = waited;
        if (!waited && waitLock.availablePermits() < 1) {
            waitLock.release();
        }
    }

    /**
     * 
     * @return {@link dead}
     * 
     * @see dead
     * @see shutdown
     */
    boolean isDead() {
        return dead;
    }

    /**
     * By setting {@link dead} variable to {@code true} makes thread to not wait for
     * {@link tasks} to get populated and ends life of thread after finishing
     * the all tasks of queue.
     * <br/>
     * Note: before setting {@code dead} this thread might be blocked by the
     * queue(when queue gets empty) so there is no guarantee that setting this
     * variable to {@code true} ends threads life.
     */
    void shutdown() {
        dead = true;
    }

    /**
     * Tries to kill the thread by calling {@link shutdown}
     * and {@link interrupt()}.
     */
    void kill() {
        shutdown();
        tasks=null;
        busy = false;
        this.interrupt();
    }

}
