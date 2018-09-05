package space.dastyar.lib.executorplus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import space.dastyar.lib.executorplus.ExecutorPlus;
import static org.junit.Assert.*;

/**
 * 
 * @since 0.1
 * @author Alireza Dastyar
 */
public abstract class ExecutorManagerTest implements AbstractTest{

    volatile ExecutorPlus ex;
    volatile List<Boolean> booleans;
    volatile int queueSize;
    volatile AtomicInteger counterForPause;
    volatile int clearCounter;

    public ExecutorManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        booleans = new ArrayList<>();
        counterForPause = new AtomicInteger(0);
        waitFor(300);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIsBusy() throws Exception {
        beginTheTest(this::inital, this::assertBusyTest);
    }

    public void beginTheTest(Runnable init, Runnable assertTheTest) {
        init.run();
        assertTheTest.run();
    }

    public void assertBusyTest() {
        assertTrue(ex.isBusy());
        for (int i = 0; i < booleans.size(); i++) {
            booleans.set(i, false);
        }
        ex.waitToFinish();
        assertFalse(ex.isBusy());
    }

    public void inital() {
        for (int i = 0; i < 10; i++) {
            int j = i;
            booleans.add(Boolean.TRUE);
            ex.execute(() -> {
                while (booleans.get(j)) {
                }
            });
        }
    }

    public void initalPause() {
        for (int i = 0; i < 10; i++) {
            int j = i;
            booleans.add(Boolean.TRUE);
            ex.execute(() -> {
                counterForPause.addAndGet(1);
                while (booleans.get(j)) {
                    waitFor(30);
                }
            });
        }
    }

    public void initalClear() {
        for (int i = 0; i < 10; i++) {
            int j = i;
            booleans.add(Boolean.TRUE);
            ex.execute(() -> {
                for (int k = 0; k < 5; k++) {
                    waitFor(100);
                }
            });
        }
    }

    @Test
    public void testGetQueueSize() {
        inital();
        waitFor(100);
        assertEquals(queueSize, ex.getQueueSize());
    }

    @Test
    public void testPauseAndResume() {
        beginTheTest(this::initalPause, this::assertPauseAndResumeTest);
    }

    public abstract void assertPauseAndResumeTest();

    @Test
    public void testClear() {
        beginTheTest(this::initalClear, this::assertClearTest);
    }

    public void assertClearTest() {
        // wait for threads to take tasks from queue
        waitFor(200);
        assertEquals(queueSize, ex.getQueueSize());
        assertTrue(ex.isBusy());
        ex.clear();
        assertEquals(0, ex.getQueueSize());
        ex.waitToFinish();
        assertFalse(ex.isBusy());
    }

    @Test
    public void testShutdown() {
        beginTheTest(this::initalClear, this::assertShutdownTest);
    }

    public void assertShutdownTest() {
        // wait for threads to take tasks from queue
        waitFor(200);
        assertEquals(queueSize, ex.getQueueSize());
        assertTrue(ex.isBusy());
        ex.shutdown();
        ex.waitToFinish();
        assertEquals(0, ex.getQueueSize());
        assertFalse(ex.isBusy());
        try {
            inital();
            fail();
        } catch (RejectedExecutionException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testShutdownNow() {
        beginTheTest(this::initalClear, this::assertShutdownNowTest);

    }

    public void assertShutdownNowTest() {
        // wait for threads to take tasks from queue
        waitFor(200);
        assertEquals(queueSize, ex.getQueueSize());
        assertTrue(ex.isBusy());
        ex.shutdownNow();
        assertFalse(ex.isBusy());
        try {
            inital();
            fail();
        } catch (RejectedExecutionException e) {
            assertNotNull(e);
        }
    }
}
