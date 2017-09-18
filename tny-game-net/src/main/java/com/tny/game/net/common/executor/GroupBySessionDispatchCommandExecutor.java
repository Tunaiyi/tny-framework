package com.tny.game.net.common.executor;

import com.tny.game.common.config.Config;
import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.common.worker.command.Command;
import com.tny.game.net.base.AppConstants;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.RunnableCommand;
import com.tny.game.net.common.dispatcher.MessageCommandBox;
import com.tny.game.net.netty.NettyAttrKeys;
import com.tny.game.net.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Unit("GroupBySessionDispatchCommandExecutor")
public class GroupBySessionDispatchCommandExecutor implements DispatchCommandExecutor {

    private static final AttrKey<ChildExecutor> COMMAND_CHILD_EXECUTOR = AttrKeys.key(Session.class + "COMMAND_CHILD_EXECUTOR");

    private static final Logger LOG_NET = LoggerFactory.getLogger(NetLogger.EXECUTOR);

    private ForkJoinPool executorService;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("DispatchCommandScheduledExecutor"));

    private static Queue<ChildExecutor> scheduledChildExecutors = new ConcurrentLinkedQueue<>();

    public GroupBySessionDispatchCommandExecutor(int threads) {
        init(threads);
    }

    public GroupBySessionDispatchCommandExecutor(Config config) {
        int threads = config.getInt(AppConstants.DISPATCHER_EXECUTOR_THREADS, Runtime.getRuntime().availableProcessors() * 2);
        this.init(threads);
    }

    private void init(int threads) {
        this.executorService = new ForkJoinPool(threads, new CoreThreadFactory("DispatchCommandExecutorPool"), null, false);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            while (!scheduledChildExecutors.isEmpty()) {
                ChildExecutor executor = scheduledChildExecutors.poll();
                if (executor != null)
                    executor.submitWhenDelay();
            }
        }, 31, 31, TimeUnit.MILLISECONDS);
    }

    @Override
    public void submit(Session session, Command command) {
        ChildExecutor executor = session.attributes().getAttribute(COMMAND_CHILD_EXECUTOR);
        if (executor == null) {
            executor = new ChildExecutor(this.executorService);
            session.attributes().setAttribute(COMMAND_CHILD_EXECUTOR, executor);
            session.attributes().setAttribute(NettyAttrKeys.USER_COMMAND_BOX, executor);
        }
        executor.accept(command);
    }

    @Override
    public void shutdown() {
        this.executorService.shutdown();
    }

    private static class ChildExecutor implements MessageCommandBox, Runnable {

        public static final int STOP = 0;
        public static final int SUBMIT = 1;
        public static final int DELAY = 2;

        private final Queue<Command> commandQueue = new ConcurrentLinkedQueue<>();

        private final ExecutorService executorService;

        private volatile AtomicInteger state = new AtomicInteger(STOP);

        private volatile Thread thread;

        private ChildExecutor(ExecutorService executorService) {
            super();
            this.executorService = executorService;
        }

        @Override
        public boolean accept(Command command) {
            if (!this.executorService.isShutdown()) {
                submit(command);
                return true;
            }
            return false;
        }

        private void submit(Command command) {
            if (this.thread != null && this.thread == Thread.currentThread()) {
                command.execute();
                if (!command.isDone())
                    this.commandQueue.offer(command);
            } else {
                this.commandQueue.offer(command);
                if (this.state.compareAndSet(STOP, SUBMIT)) {
                    this.executorService.submit(this);
                }
            }
        }

        @Override
        public boolean accept(Runnable runnable) {
            return accept(new RunnableCommand(runnable));
        }

        @Override
        public void run() {
            this.thread = Thread.currentThread();
            while (true) {
                Command cmd = this.commandQueue.peek();
                if (cmd != null) {
                    try {
                        cmd.execute();
                    } catch (Throwable e) {
                        LOG_NET.error("run command task {} exception", cmd.getName(), e);
                    }
                    if (cmd.isDone()) {
                        this.commandQueue.poll();
                    } else {
                        this.state.set(DELAY);
                        scheduledChildExecutors.add(this);
                        break;
                    }
                } else {
                    this.state.set(STOP);
                    this.trySubmit();
                    break;
                }
            }
            this.thread = null;
        }

        private void trySubmit() {
            if (!this.commandQueue.isEmpty() && this.state.compareAndSet(STOP, SUBMIT)) {
                this.executorService.submit(this);
            }
        }

        private void submitWhenDelay() {
            if (this.state.get() == DELAY)
                this.executorService.submit(this);
        }

        @Override
        public int size() {
            return this.commandQueue.size();
        }

    }
}
