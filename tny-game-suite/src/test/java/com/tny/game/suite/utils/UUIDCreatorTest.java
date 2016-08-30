package com.tny.game.suite.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Kun Yang on 16/8/14.
 */
public class UUIDCreatorTest {

    private static final int createSize = 1000000;
    private static final int threadSize = 8;
    private static final UUIDCreator creator = new UUIDCreator(1);
    private static final CountDownLatch startLatch = new CountDownLatch(threadSize);
    private static final CountDownLatch finishLatch = new CountDownLatch(threadSize);


    static class Task {

        List<Long> ids = new ArrayList<>(createSize);

        void run() {
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int index = 0; index < createSize; index++) {
                ids.add(creator.createID());
            }
            finishLatch.countDown();
        }

    }

    @Test
    public void createID() throws Exception {
        System.out.println(System.currentTimeMillis());
        SortedSet<Long> ids = new TreeSet<>();
        for (int index = 0; index < 10000; index++) {
            long id;
            if (!ids.add(id = creator.createID()))
                System.out.println("fail : " + id);
            else
                System.out.println(id);
        }
    }

    @Test
    public void createConcurrentID() throws Exception {
        ExecutorService service = Executors.newCachedThreadPool();
        Task[] tasks = new Task[threadSize];
        for (int index = 0; index < threadSize; index++) {
            tasks[index] = new Task();
            service.submit(tasks[index]::run);
            startLatch.countDown();
        }
        SortedSet<Long> ids = new TreeSet<>();
        finishLatch.await();
        for (Task task : tasks) {
            task.ids.forEach(id -> {
                if (!ids.add(id))
                    System.out.println("fail : " + id);
            });
        }
    }

}