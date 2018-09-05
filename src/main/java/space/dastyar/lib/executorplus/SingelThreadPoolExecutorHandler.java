package space.dastyar.lib.executorplus;

/**
 * SingelThreadPoolExecutor implementation of {@link ExecutorPlus}.
 * 
 * @since 0.1
 * @author Alireza Dastyar
 */
 class SingelThreadPoolExecutorHandler extends ExecuteHandler {

    public SingelThreadPoolExecutorHandler() {
        super(1);
    }
}
