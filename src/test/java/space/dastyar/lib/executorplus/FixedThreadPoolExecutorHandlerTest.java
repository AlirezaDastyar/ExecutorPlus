package space.dastyar.lib.executorplus;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import space.dastyar.lib.executorplus.ExecutorPlusFactory;
import static org.junit.Assert.*;

/**
 *
 * @since 0.1
 * @author Alireza Dastyar
 */
public class FixedThreadPoolExecutorHandlerTest extends ExecuteHandlerTest {

    public FixedThreadPoolExecutorHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        super.setUp();
        ex = ExecutorPlusFactory.newFixedThreadPool(4);
        queueSize = 6;
    }

    @After
    public void tearDown() {
        ex = null;
    }

    @Override
    public void assertPauseAndResumeTest() {
        waitFor(100);
        ex.pause();
        for (int i = 0; i < booleans.size(); i++) {
            booleans.set(i, false);
        }
        assertEquals(4, counterForPause.get());
        ex.resume();
        waitFor(100);
        assertEquals(10, counterForPause.get());
    }

}
