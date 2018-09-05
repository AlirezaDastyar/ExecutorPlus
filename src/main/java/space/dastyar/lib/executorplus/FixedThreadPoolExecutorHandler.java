package space.dastyar.lib.executorplus;

/**
 * FixedThreadPoolExecutor implementation of {@link ExecutorPlus}.
 * 
 * @since 0.1
 * @author Alireza Dastyar
 */
 class FixedThreadPoolExecutorHandler extends ExecuteHandler {

    public FixedThreadPoolExecutorHandler(int size) {
        super(size);
    }

}
