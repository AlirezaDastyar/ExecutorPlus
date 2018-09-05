package space.dastyar.lib.executorplus;

/**
 *
 * @since 0.1
 * @author Alireza Dastyar
 */
public interface AbstractTest {

    public default void waitFor(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ex1) {
            Thread.currentThread().interrupt();
        }
    }
}
