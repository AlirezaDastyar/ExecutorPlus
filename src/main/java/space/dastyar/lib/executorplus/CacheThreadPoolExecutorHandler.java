package space.dastyar.lib.executorplus;

/**
 * CacheThreadPoolExecutor implementation of {@link ExecutorPlus}.
 * 
 * @since 0.1
 * @author Alireza Dastyar
 */
class CacheThreadPoolExecutorHandler extends ExecuteHandler  {

    /**
     * Checks queue's size and create a new worker thread if needed.
     */
    @Override
    protected void start() {
        waitFor(100);
        if (getQueueSize() > 0) {
            boolean isHandeled = false;
            if (!isHandeled) {
                createAndStartWorker();
                setThreadCount(getThreadCount() + 1);
            }
        }
    }

}
