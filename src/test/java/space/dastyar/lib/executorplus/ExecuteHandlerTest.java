package space.dastyar.lib.executorplus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @since 0.1
 * @author Alireza Dastyar
 */
public abstract class ExecuteHandlerTest extends ExecutorManagerTest {

    List<Integer> exResults;
    List<Future> futures;
    Collection<Callable<Integer>> collections;
    int logOf = 100;

    public ExecuteHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        System.out.println("new test settedup");
        super.setUp();
        exResults = new ArrayList<>();
        futures = new ArrayList<>();
        collections = new ArrayList<>();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testExecute() {
        beginTheTest(this::initialExecute, this::assertExecutionTest);
    }

    public void initialExecute() {
        for (int i = 0; i < 10; i++) {
            int j = i;
            ex.execute(() -> {
                exResults.add((int) Math.log10(j * logOf));
            });
        }
    }

    public void assertExecutionTest() {
        ex.waitToFinish();
        for (int i = 0; i < 10; i++) {
            boolean contains = exResults.contains((int) Math.log10(i * logOf));
            assertTrue(contains);
        }
    }

    @Test
    public void testSubmit_Runnable() {
        beginTheTest(this::initialSubmit, this::assertRunnableSubmitTest);
    }

    public void initialSubmit() {
        for (int i = 0; i < 10; i++) {
            int j = i;
            Future future = ex.submit(() -> {
                exResults.add((int) Math.log10(j * logOf));
            });
            futures.add(future);
        }
    }

    public void assertRunnableSubmitTest() {
        ex.waitToFinish();
        for (int i = 0; i < 10; i++) {
            boolean contains = exResults.contains((int) Math.log10(i * logOf));
            assertTrue(contains);
            try {
                assertNull(futures.get(i).get());
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("SubmitRunnableTest failed on future.get()");
            }
        }
        assertExecutionTest();
    }

    @Test
    public void testSubmit_Runnable_GenericType() {
        beginTheTest(this::initialRunnableSubmit, this::assertSubmitRunnableWithResultTest);
    }

    public void initialRunnableSubmit() {
        for (int i = 0; i < 10; i++) {
            int j = i;
            Future future = ex.submit(() -> {
                exResults.add((int) Math.log10(j * logOf));
            }, exResults);
            futures.add(future);
        }
    }

    public void assertSubmitRunnableWithResultTest() {
        ex.waitToFinish();
        for (int i = 0; i < futures.size(); i++) {
            Future future = futures.get(i);
            try {
                List<Integer> lst = (List<Integer>) future.get();
                assertEquals(10, lst.size());
            } catch (Exception e) {
                e.printStackTrace();
                fail("SubmitRunnableWithResultTest failed on future.get()");
            }
        }
    }

    @Test
    public void testSubmit_Callable() {
        beginTheTest(this::initialCallableSubmit, this::assertCallableSubmitTest);
    }

    public void initialCallableSubmit() {
        for (int i = 0; i < 10; i++) {
            int j = i;
            Future future = ex.submit(() -> {
                return exResults.add((int) Math.log10(j * logOf));
            });
            futures.add(future);
        }
    }

    public void assertCallableSubmitTest() {
        ex.waitToFinish();
        for (int i = 0; i < futures.size(); i++) {
            Future future = futures.get(i);
            try {
                assertTrue((boolean) future.get());
            } catch (Exception e) {
                e.printStackTrace();
                fail("CallableSubmitTest failed with on future.get()");
            }
        }
    }

    @Test
    public void invokAnyTest() {
        beginTheTest(this::invokAnyInitialNormal, this::assertInvokAnyNormal);
    }

    public void invokAnyInitialNormal() {
        collections.add(() -> 0);
        initCallableLog(10);
    }

    protected void initCallableLog(float d) {
        for (int i = 0; i < 9; i++) {
            float j = i * d;
            collections.add(() -> (int) Math.log(j * logOf));
        }
    }

    public void assertInvokAnyNormal() {
        try {
            int a = ex.invokAny(collections);
            assertEquals(0, a);
        } catch (Exception e) {
            fail("InvokAny thorwed an exception!\n"+e.getMessage());
        }
    }

    @Test
    public void invokAnyTestWithExecutionException() {
        beginTheTest(this::invokAnyInitialExecutionException,
                this::assertInvokAnyExecutionException);
    }

    public void invokAnyInitialExecutionException() {
        collections.add(() -> {
            throw new RuntimeException();
        });
    }

    public void assertInvokAnyExecutionException() {
        try {
            ex.invokAny(collections);
            fail("No execution exception has been thrown!");
        } catch (ExecutionException e) {
            assertNotNull(e);
        } catch (InterruptedException ex) {
            fail("InvokAny intrupted!");
        }
    }

    @Test
    public void invokAnyTestWithTimeoutException() {
        beginTheTest(this::invokAnyInitialTimeoutException,
                this::assertInvokAnyTimeoutException);
    }

    public void invokAnyInitialTimeoutException() {
        for (int i = 0; i < 2; i++) {
            collections.add(() -> {
                waitFor(1000000);
                return 0;
            });
        }
    }

    public void assertInvokAnyTimeoutException() {
        try {
            ex.invokAny(collections, 10L, TimeUnit.SECONDS);
            fail("No timeout exception has been thrown!");
        } catch (ExecutionException e) {
            fail("execution exception has been thrown!");
        } catch (InterruptedException ex) {
            fail("InvokAny intrupted!");
        } catch (TimeoutException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void invokAllTestNormal() {
        beginTheTest(this::invokAllNormal,
                this::assertInvokAllNormal);
    }

    public void invokAllNormal() {
        initCallableLog(0.1f);
    }

    public void assertInvokAllNormal() {
        List<Future<Integer>> futures = ex.invokAll(collections);
        List<Integer> ints = toIntList(futures);
        boolean flag = true;
        for (int i = 0; i < 9; i++) {
            flag = flag && ints.contains((int) Math.log(i * logOf * 0.1f));
        }
        assertTrue(flag);
    }

    private List<Integer> toIntList(List<Future<Integer>> futures) {
        return futures.stream().map(x -> {
            try {
                return x.get();
            } catch (Exception ex) {
                System.out.println("null");
                return null;
            }
        }).collect(Collectors.toList());
    }

    @Test
    public void invokAllTestWithTimeoutException() {
        beginTheTest(this::invokAllInitialTimeout,
                this::assertInvokAllTimeout);
    }

    public void invokAllInitialTimeout() {
        for (int i = 0; i < 2; i++) {
            collections.add(() -> {
                waitFor(1000000);
                return 0;
            });
        }
    }

    public void assertInvokAllTimeout() {
        List<Future<Integer>> futures1 = ex.invokAll(collections, 10L, TimeUnit.SECONDS);
        for (Future future : futures1) {
            if (future.isDone()) {
                fail("Should not be done before 10 seconds");
            }
        }
    }
}
