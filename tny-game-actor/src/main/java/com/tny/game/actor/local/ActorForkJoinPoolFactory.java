package com.tny.game.actor.local;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kun Yang on 16/4/26.
 */
public class ActorForkJoinPoolFactory implements ForkJoinPoolFactory {

    @Override
    public ForkJoinPool createForkJoinPool() {
        return null;
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
