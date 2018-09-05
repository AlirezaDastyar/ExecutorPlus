package space.dastyar.lib.executorplus;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Uses the {@link FutureTask} as underling implementation
 * of {@link Future} interface.
 * 
 * @since 0.1
 * @author Alireza Dastyar
 */
 class FutureSimulator<T> implements Future<T> {

    private final FutureTask<T> futureTask;

    public FutureSimulator(FutureTask<T> futureTask) {
        this.futureTask = futureTask;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return futureTask.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return futureTask.isCancelled();
    }

    @Override
    public boolean isDone() {
        return futureTask.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return futureTask.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return futureTask.get(timeout, unit);
    }

}
