package com.tny.game.net.command;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrUtils;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.common.utils.collection.LinkedTransferQueue;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.common.dispatcher.MessageCommandBox;
import com.tny.game.net.netty.NettyAttrKeys;
import com.tny.game.net.session.Session;
import com.tny.game.worker.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPoolCommandExecutor implements MessageCommandExecutor {

    private static final AttrKey<ChildExecutor> COMMAND_CHILD_EXECUTOR = AttrUtils.key(Session.class + "COMMAND_CHILD_EXECUTOR");

    private static final Logger LOG_NET = LoggerFactory.getLogger(NetLogger.EXECUTOR);

    private final ExecutorService executorService;

    public ThreadPoolCommandExecutor() {
        int corePoolSize = Integer.parseInt(System.getProperty("tny.server.executor.corePoolSize", "4"));
        int maximumPoolSize = Integer.parseInt(System.getProperty("tny.server.executor.maximumPoolSize", "8"));
        long keepAliveTime = Integer.parseInt(System.getProperty("tny.server.executor.keepAliveTime", "720000"));
        this.executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedTransferQueue<>(), new CoreThreadFactory(
                "ThreadPoolCommandExecutorPool"));
    }

    public ThreadPoolCommandExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        this.executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedTransferQueue<>(), new CoreThreadFactory(
                "ThreadPoolCommandExecutorPool"));
    }

    @Override
    public void submit(Session session, MessageCommand<?> command) {
        ChildExecutor executor = session.attributes().getAttribute(COMMAND_CHILD_EXECUTOR);
        if (executor == null) {
            executor = new ChildExecutor(this.executorService);
            session.attributes().setAttribute(COMMAND_CHILD_EXECUTOR, executor);
            session.attributes().setAttribute(NettyAttrKeys.USER_COMMAND_BOX, executor);
        }
        executor.appoint(command);
    }

    @Override
    public void shutdown() {
        this.executorService.shutdown();
    }

    private static class ChildExecutor implements MessageCommandBox, Runnable {

        private final Queue<MessageCommand<?>> commandQueue = new ConcurrentLinkedQueue<>();

        private final ExecutorService executorService;

        private volatile AtomicBoolean start = new AtomicBoolean(false);

        private volatile Thread thread;

        private ChildExecutor(ExecutorService executorService) {
            super();
            this.executorService = executorService;
        }

        @Override
        public boolean appoint(MessageCommand<?> command) {
            return this.appoint(command, null);
        }

        @Override
        public <T> boolean appoint(MessageCommand<T> command, Callback<T> callback) {
            if (!this.executorService.isShutdown()) {
                if (callback != null)
                    command.setCallback(callback);
                submit(command);
                return true;
            }
            return false;
        }

        private <T> void submit(MessageCommand<T> command) {
            if (this.thread != null && this.thread == Thread.currentThread()) {
                command.execute();
                if (!command.isDone())
                    this.commandQueue.offer(command);
            } else {
                this.commandQueue.offer(command);
                if (this.start.compareAndSet(false, true)) {
                    this.executorService.submit(this);
                }
            }
        }

        @Override
        public boolean appoint(Runnable runnable) {
            return appoint(new RunnableMessageCommand(runnable), null);
        }

        @Override
        public void run() {
            this.thread = Thread.currentThread();
            for (; ; ) {
                MessageCommand<?> cmd = this.commandQueue.peek();
                if (cmd != null) {
                    try {
                        cmd.execute();
                    } catch (Exception e) {
                        LOG_NET.error("run command task {} exception", cmd.getName(), e);
                    }
                    if (cmd.isDone()) {
                        this.commandQueue.poll();
                    } else {
                        this.executorService.submit(this);
                        break;
                    }
                } else {
                    if (this.start.compareAndSet(true, false)) {
                        if (this.commandQueue.isEmpty() && !this.start.compareAndSet(false, true))
                            break;
                    }
                }
            }
            this.thread = null;
        }

        @Override
        public int size() {
            return this.commandQueue.size();
        }

    }
}
