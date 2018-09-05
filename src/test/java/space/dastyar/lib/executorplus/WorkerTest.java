/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package space.dastyar.lib.executorplus;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @since 0.1
 * @author Alireza Dastyar
 */
public class WorkerTest implements AbstractTest{
  
    Worker worker;
    BlockingQueue<Runnable> queue;
    
    @Before
    public void setUp() {
        queue=new LinkedBlockingQueue();
        worker=new Worker(queue);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWaited() {
        // wait is for simulating a task  
        queue.add(()->{waitFor(500);});
        worker.setWaited(true);
        worker.start();
        assertFalse(worker.isBusy());
        assertEquals(1, queue.size());
        worker.setWaited(false);
        // giving the worker thread some time . 
        waitFor(100);
        assertTrue(worker.isBusy());
        assertEquals(0, queue.size());
    }
    
    @Test
    public void testQueueBlocking() {
        // wait is for simulating a task  
        queue.add(()->{waitFor(500);});
        worker.start();
        // giving the worker thread some time . 
        waitFor(100);
        assertTrue(worker.isBusy());
        assertEquals(0, queue.size());
        waitFor(500);
        assertFalse(worker.isBusy());
    }
    
    @Test
    public void testAfterQueueBlocking() {
        worker.start();
        assertEquals(0, queue.size());
        assertFalse(worker.isBusy());
        addTask();
        // giving the worker thread some time . 
        waitFor(100);
        assertTrue(worker.isBusy());
    }

    private void addTask() {
        queue.add(()->{waitFor(500);});
    }
    
    @Test
    public void testShutdown() {
        for (int i = 0; i < 2; i++) {
             addTask();
        }
        worker.start();
        worker.shutdown();
        assertFalse(worker.isDone());
        // wait for for all task to finish.
        waitFor(1100);
        assertFalse(worker.isBusy());
        assertTrue(worker.isDone());
    }
    
    @Test
    public void testKill() {
        for (int i = 0; i < 2; i++) {
             addTask();
        }
        worker.start();
        assertFalse(worker.isDone());
        worker.kill();
        // giving the worker thread some time . 
        waitFor(600);
        assertFalse(worker.isBusy());
        assertTrue(worker.isDone());
    }
}
