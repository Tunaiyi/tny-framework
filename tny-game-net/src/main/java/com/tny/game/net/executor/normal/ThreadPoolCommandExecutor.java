package com.tny.game.net.executor.normal;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttributeUtils;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.common.utils.collection.LinkedTransferQueue;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.dispatcher.DispatchCommandTask;
import com.tny.game.net.dispatcher.DispatcherCommand;
import com.tny.game.net.dispatcher.NetAttributeKey;
import com.tny.game.net.dispatcher.Session;
import com.tny.game.net.dispatcher.command.UserCommand;
import com.tny.game.net.dispatcher.command.UserCommandBox;
import com.tny.game.net.executor.DispatcherCommandExecutor;
import com.tny.game.worker.Callback;
import com.tny.game.worker.command.CommandTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPoolCommandExecutor implements DispatcherCommandExecutor {

    public static final AttrKey<ChildExecutor> COMMAND_CHILD_EXECUTOR = AttributeUtils.key(Session.class + "COMMAND_CHILD_EXECUTOR");

    private static final Logger LOG_NET = LoggerFactory.getLogger(CoreLogger.EXECUTOR);

    private final ExecutorService executorService;

    public ThreadPoolCommandExecutor() {
        int corePoolSize = Integer.parseInt(System.getProperty("tny.server.executor.corePoolSize", "4"));
        int maximumPoolSize = Integer.parseInt(System.getProperty("tny.server.executor.maximumPoolSize", "8"));
        long keepAliveTime = Integer.parseInt(System.getProperty("tny.server.executor.keepAliveTime", "720000"));
        this.executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedTransferQueue<Runnable>(), new CoreThreadFactory(
                "ThreadPoolCommandExecutorPool"));
    }

    public ThreadPoolCommandExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        this.executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedTransferQueue<Runnable>(), new CoreThreadFactory(
                "ThreadPoolCommandExecutorPool"));
    }

    @Override
    public void sumit(DispatcherCommand<?> command) {
        Session session = command.getSession();
        ChildExecutor executor = session.attributes().getAttribute(COMMAND_CHILD_EXECUTOR);
        if (executor == null) {
            executor = new ChildExecutor(this.executorService);
            session.attributes().setAttribute(COMMAND_CHILD_EXECUTOR, executor);
            session.attributes().setAttribute(NetAttributeKey.USER_COMMAND_BOX, executor);
        }
        executor.add(command);
    }

    @Override
    public void shutdown() {
        this.executorService.shutdown();
    }

    private static class ChildExecutor implements UserCommandBox, Runnable {

        private final Queue<CommandTask<?>> commandQueue = new ConcurrentLinkedQueue<CommandTask<?>>();

        private final ExecutorService executorService;

        private volatile AtomicBoolean start = new AtomicBoolean(false);

        private volatile Thread thread;

        private ChildExecutor(ExecutorService executorService) {
            super();
            this.executorService = executorService;
        }

        public void add(final DispatcherCommand<?> command) {
            if (!this.executorService.isShutdown()) {
                this.commandQueue.offer(command);
                if (this.start.compareAndSet(false, true)) {
                    this.executorService.submit(this);
                }
            }
        }

        @Override
        public boolean appoint(UserCommand<?> command) {
            return this.appoint(command, null);
        }

        @Override
        public boolean addCommand(UserCommand<?> command) {
            if (!this.executorService.isShutdown()) {
                CommandTask<?> commandTask = new DispatchCommandTask<>(command, null);
                this.commandQueue.offer(commandTask);
                if (this.start.compareAndSet(false, true)) {
                    this.executorService.submit(this);
                }
                return true;
            }
            return false;
        }

        @Override
        public <T> boolean appoint(UserCommand<T> command, Callback<T> callback) {
            if (!this.executorService.isShutdown()) {
                CommandTask<?> commandTask = new DispatchCommandTask<T>(command, callback);
                if (this.thread != null && this.thread == Thread.currentThread() && commandTask.getCommand().isCanExecute()) {
                    commandTask.run();
                } else {
                    this.commandQueue.offer(commandTask);
                    if (this.start.compareAndSet(false, true)) {
                        this.executorService.submit(this);
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public void run() {
            this.thread = Thread.currentThread();
            for (; ; ) {
                CommandTask<?> task = this.commandQueue.poll();
                if (task != null && task.getCommand().isCanExecute()) {
                    try {
                        task.run();
                    } catch (Exception e) {
                        LOG_NET.error("run command task {} exception", task.getCommand().getName(), e);
                    }
                } else {
                    if (this.start.compareAndSet(true, false)) {
                        if (!this.commandQueue.isEmpty() && this.start.compareAndSet(false, true)) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
            }
            this.thread = null;
        }

        @Override
        public int size() {
            return this.commandQueue.size();
        }

        //		public String getName() {
        //			return name;
        //		}
        //
        //		public Thread getWorkerThread() {
        //			return thread;
        //		}
        //
        //		public boolean isRunning() {
        //			return thread != null;
        //		}

        @Override
        public boolean isEmpty() {
            return this.commandQueue.isEmpty();
        }

        @Override
        public void clear() {
            this.commandQueue.clear();
        }

        @Override
        public int getRunSize() {
            return 0;
        }

        @Override
        public long getRunUseTime() {
            return 0;
        }

    }
}
