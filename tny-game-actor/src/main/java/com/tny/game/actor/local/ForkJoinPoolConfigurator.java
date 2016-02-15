package com.tny.game.actor.local;

import com.tny.game.common.config.Config;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ForkJoinPool配置接口
 * Created by Kun Yang on 16/1/19.
 */
public class ForkJoinPoolConfigurator {

    private Config config;


    public ForkJoinPoolConfigurator(Config config) {
        this.config = config;
    }

    private int scaledPoolSize(int min, double factor, int max) {
        return Math.min(Math.max((int) Math.ceil((Runtime.getRuntime().availableProcessors() * factor)), min), max);
    }

    public ForkJoinPoolFactory createForkJoinPoolFactory(String id, Thread.UncaughtExceptionHandler handler) {
        return new ActorForkJoinPoolFactory(id, scaledPoolSize(
                config.getInt("parallelism-min", 4),
                config.getDouble("parallelism-factor", 2.0),
                config.getInt("parallelism-factor", 32)),
                config.getBoolean("thread-daemon", false),
                config.getBoolean("async-mode", true),
                handler);
    }

    private static class ActorForkJoinPoolFactory implements ForkJoinPoolFactory {

        private ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory;
        private int parallelism;
        private boolean asyncMode;

        private ActorForkJoinPoolFactory(String id, int parallelism, boolean daemon, boolean asyncMode, Thread.UncaughtExceptionHandler handler) {
            this.parallelism = parallelism;
            this.asyncMode = asyncMode;
            this.threadFactory = new ActorForkJoinWorkerThreadFactory(id, daemon, handler);
        }

        public ForkJoinPool createForkJoinPool() {
            return new ForkJoinPool(parallelism, threadFactory, null, this.asyncMode);
        }
    }

    private static class ActorForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        private String name;

        private boolean daemon;

        private Thread.UncaughtExceptionHandler handler;

        private AtomicInteger idCreator = new AtomicInteger(0);

        private ActorForkJoinWorkerThreadFactory(String name, boolean daemon, Thread.UncaughtExceptionHandler handler) {
            this.name = name;
            this.daemon = daemon;
            this.handler = handler;
        }

        private String createName() {
            return "Actor-ForkJoinPool-[" + name + "]-" + idCreator.incrementAndGet();
        }

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new ActorForkJoinWorkerThread(pool, createName(), daemon, handler);
        }
    }


    private static class ActorForkJoinWorkerThread extends ForkJoinWorkerThread {

        /**
         * Creates a ForkJoinWorkerThread operating in the given pool.
         *
         * @param pool the pool this thread works in
         * @throws NullPointerException if pool is null
         */
        public ActorForkJoinWorkerThread(ForkJoinPool pool, String name, boolean daemon, UncaughtExceptionHandler handler) {
            super(pool);
            this.setName(name);
            this.setDaemon(daemon);
            this.setUncaughtExceptionHandler(handler);
        }

    }

}

