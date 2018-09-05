package space.dastyar.lib.executorplus;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 *
 * @since 0.1
 * @author Alireza Dastyar
 */
public class CacheThreadPoolExecutorHandlerTest extends ExecuteHandlerTest {

    public CacheThreadPoolExecutorHandlerTest() {
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
        ex = ExecutorPlusFactory.newCachedThreadPool();
        queueSize = 0;
    }

    @After
    public void tearDown() {
        ex = null;
    }

    @Test
    public void testGetThreadCounts() {
        inital();
        assertEquals(10, ex.getThreadCount());
        for (int i = 0; i < 5; i++) {
            booleans.set(i, Boolean.FALSE);
        }
        for (int i = 0; i < 3; i++) {
            int j = i;
            ex.execute(() -> {
                while (booleans.get(j)) {
                    waitFor(100);
                }
            });
        }
        assertEquals(10, ex.getThreadCount());
    }

    public void assertPauseAndResumeTest() {
        waitFor(100);
        ex.pause();
        for (int i = 0; i < booleans.size(); i++) {
            booleans.set(i, false);
        }
        assertEquals(10, counterForPause.get());
        ex.resume();
        waitFor(100);
        assertEquals(10, counterForPause.get());
    }
    
    @Test
    public void startTest(){
        CacheThreadPoolExecutorHandler ctp=spy(CacheThreadPoolExecutorHandler.class);
        when(ctp.getQueueSize()).thenReturn(1);
        ctp.start();
        verify(ctp).createAndStartWorker();
        verify(ctp).setThreadCount(1);
    
    }
}
