/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.common.concurrent;

import javax.annotation.Nonnull;
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

    public static CoreThreadFactory withName(String namePrefix) {
        return new CoreThreadFactory(namePrefix, null, false);
    }

    public static CoreThreadFactory daemonWithName(Class<?> clazz) {
        return new CoreThreadFactory(clazz.getSimpleName(), null, true);
    }

    public static CoreThreadFactory daemonWithName(String namePrefix) {
        return new CoreThreadFactory(namePrefix, null, true);
    }

    public static CoreThreadFactory with(String namePrefix, UncaughtExceptionHandler handler, boolean daemon) {
        return new CoreThreadFactory(namePrefix, handler, daemon);
    }

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
        //        SecurityManager localSecurityManager = System.getSecurityManager();
        //        this.group = ((localSecurityManager != null) ? localSecurityManager.getThreadGroup() : Thread.currentThread().getThreadGroup());
        this.group = new ThreadGroup(namePrefix + " Group");
        this.namePrefix = namePrefix + "-thread-";
        this.daemon = daemon;
        this.handler = handler;
    }

    @Override
    public Thread newThread(@Nonnull Runnable paramRunnable) {
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
