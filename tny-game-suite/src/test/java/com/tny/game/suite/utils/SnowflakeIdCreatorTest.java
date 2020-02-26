package com.tny.game.suite.utils;

import com.tny.game.common.utils.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Kun Yang on 16/8/14.
 */
public class SnowflakeIdCreatorTest {

    private static final int createSize = 1000000;
    private static final int threadSize = 8;
    private static final SnowflakeIdCreator creator = new SnowflakeIdCreator(1);
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
                this.ids.add(creator.createId());
            }
            finishLatch.countDown();
        }

    }

    @Test
    public void createId() throws Exception {
        System.out.println(System.currentTimeMillis());
        SortedSet<Long> ids = new TreeSet<>();
        for (int index = 0; index < 10000; index++) {
            long id;
            if (!ids.add(id = creator.createId()))
                System.out.println("fail : " + id);
            else
                System.out.println(id);
        }
    }

    @Test
    public void createConcurrentId() throws Exception {
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