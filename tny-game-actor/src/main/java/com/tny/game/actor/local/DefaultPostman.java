package com.tny.game.actor.local;

import com.tny.game.actor.Envelope;
import com.tny.game.actor.SystemMessage;
import com.tny.game.actor.event.Error;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * 默认信息分发者
 * Created by Kun Yang on 16/1/21.
 */
public class DefaultPostman extends MessagePostman {

    private String id;
    private int throughput;
    private Duration throughputDeadlineTime;
    private ForkJoinPoolConfigurator forkJoinPoolConfigurator;
    private Duration shutdownTimeout;
    private volatile ExecutorServiceDelegate executorService;

    public DefaultPostman(PostmanConfigurator configurator, String id, int throughput, Duration throughputDeadlineTime,
                          ForkJoinPoolConfigurator forkJoinPoolConfigurator, Duration shutdownTimeout) {
        super(configurator);
        this.id = id;
        this.throughput = throughput;
        this.throughputDeadlineTime = throughputDeadlineTime;
        this.forkJoinPoolConfigurator = forkJoinPoolConfigurator;
        this.shutdownTimeout = shutdownTimeout;
        this.executorService = createServiceDelegate();
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    protected Duration getShutdownTimeout() {
        return shutdownTimeout;
    }

    @Override
    protected int getThroughput() {
        return throughput;
    }

    @Override
    protected Duration getThroughputDeadlineTime() {
        return throughputDeadlineTime;
    }

    @Override
    public void post(ActorCell receiver, Envelope envelope) {
        Postbox postbox = receiver.getPostbox();
        postbox.enqueue(receiver.self(), envelope);
        registerForExecution(postbox, true, false);
    }

    @Override
    public void systemPost(ActorCell receiver, SystemMessage message) {
        Postbox postbox = receiver.getPostbox();
        postbox.systemEnqueue(receiver.self(), message);
        registerForExecution(postbox, false, true);
    }


    @Override
    public void executeTask(TaskInvocation task) {
        try {
            executorService.execute(task);
        } catch (RejectedExecutionException e) {
            try {
                executorService.execute(task);
            } catch (RejectedExecutionException e2) {
                eventStream.publish(Error.error(e2, getClass().getName(), getClass(), "executeTask was rejected twice!"));
                throw e2;
            }
        }
    }


    @Override
    protected Postbox createPostbox(Cell actor, PostboxFactory factory) {
        return factory.createPostbox(actor.self(), actor.getSystem());
    }

    @Override
    protected void shutdown() {
        ExecutorServiceDelegate newDelegate = createServiceDelegate();
        ExecutorServiceDelegate oldDelegate;
        synchronized (this) {
            oldDelegate = executorService;
            executorService = newDelegate;
        }
        oldDelegate.shutdown();
    }

    @Override
    protected boolean registerForExecution(Postbox postbox, boolean hasMessageHint, boolean hasSystemMessageHint) {
        if (postbox.canBeScheduledForExecution(hasMessageHint, hasSystemMessageHint)) {
            if (postbox.setAsScheduled()) {
                try {
                    executorService.execute(postbox);
                    return true;
                } catch (RejectedExecutionException e) {
                    try {
                        executorService.execute(postbox);
                        return true;
                    } catch (RejectedExecutionException e2) {
                        postbox.setAsIdle();
                        eventStream.publish(Error.error(e, getClass().getName(), getClass(), "registerForExecution was rejected twice!"));
                        throw e;
                    }
                }
            }
        }
        return false;
    }

    private ExecutorServiceDelegate createServiceDelegate() {
        return new LazyExecutorServiceDelegate(
                this.forkJoinPoolConfigurator.createForkJoinPoolFactory(this.id, prerequisites.getUncaughtExceptionHandler()));
    }

    private static class LazyExecutorServiceDelegate implements ExecutorServiceDelegate {

        private ForkJoinPoolFactory factory;

        private ExecutorService executorService;

        private LazyExecutorServiceDelegate(ForkJoinPoolFactory factory) {
            this.factory = factory;
        }

        @Override
        public ExecutorService executorService() {
            if (executorService != null)
                return executorService;
            synchronized (this) {
                if (executorService != null)
                    return executorService;
                executorService = factory.createForkJoinPool();
            }
            return executorService;
        }
    }
}
