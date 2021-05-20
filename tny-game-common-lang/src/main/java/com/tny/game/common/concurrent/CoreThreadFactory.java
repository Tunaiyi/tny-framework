package com.tny.game.common.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.*;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CoreThreadFactory implements ThreadFactory, ForkJoinWorkerThreadFactory {

    /**
     * @uml.property name="group"
     */
    private final ThreadGroup group;
    /**
     * @uml.property name="threadNumber"
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * @uml.property name="namePrefix"
     */
    private final String namePrefix;
    /**
     * @uml.property name="daemon"
     */
    private final boolean daemon;

    private final UncaughtExceptionHandler handler;

    public CoreThreadFactory(String namePrefix) {
        this(namePrefix, null, false);
    }

    public CoreThreadFactory(String namePrefix, boolean daemon) {
        this(namePrefix, null, daemon);
    }

    public CoreThreadFactory(String namePrefix, UncaughtExceptionHandler handler) {
        this(namePrefix, handler, false);
    }

    public CoreThreadFactory(String namePrefix, UncaughtExceptionHandler handler, boolean daemon) {
        SecurityManager localSecurityManager = System.getSecurityManager();
        this.group = ((localSecurityManager != null) ? localSecurityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
        this.namePrefix = namePrefix + "-thread-";
        this.daemon = daemon;
        this.handler = handler;
    }

    @Override
    public Thread newThread(Runnable paramRunnable) {
        Thread localThread = new Thread(this.group, paramRunnable, threadName(), 0L);
        localThread.setDaemon(this.daemon);
        if (localThread.getPriority() != 5) {
            localThread.setPriority(5);
        }
        return localThread;
    }

    private String threadName() {
        return this.namePrefix + this.threadNumber.getAndIncrement();
    }

    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        return new CoreForkJoinWorkerThread(pool, threadName(), this.daemon, this.handler);
    }

    private static class CoreForkJoinWorkerThread extends ForkJoinWorkerThread {

        /**
         * Creates a ForkJoinWorkerThread operating in the given pool.
         *
         * @param pool the pool this thread works in
         * @throws NullPointerException if pool is null
         */
        public CoreForkJoinWorkerThread(ForkJoinPool pool, String name, boolean daemon, UncaughtExceptionHandler handler) {
            super(pool);
            this.setName(name);
            this.setDaemon(daemon);
            this.setUncaughtExceptionHandler(handler);
        }

    }

    // public static void main(String[] args) {
    //     Instant instant = Instant.now();
    //     System.out.println(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
    //     System.out.println(ZonedDateTime.ofInstant(instant, ZoneId.of("+8")));
    //     System.out.println(ZonedDateTime.ofInstant(instant, ZoneId.of("+7")).plusHours(1));
    // }

}
