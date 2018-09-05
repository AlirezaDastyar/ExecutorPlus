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
public class SingelThreadPoolExecutorHandlerTest extends ExecuteHandlerTest {

    public SingelThreadPoolExecutorHandlerTest() {
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
        ex = ExecutorPlusFactory.newSingelThreadPool();
        queueSize = 9;
    }

    @After
    public void tearDown() {
        super.tearDown();
        ex = null;
    }

   
    public void assertPauseAndResumeTest() {
        waitFor(100);
        ex.pause();
        for (int i = 0; i < booleans.size(); i++) {
            booleans.set(i, Boolean.FALSE);
        }
        waitFor(100);
        assertEquals(1, counterForPause.get());
        ex.resume();
        waitFor(200);
        assertEquals(10, counterForPause.get());
    }

}
