package com.tny.game.net.command.executor;

import com.tny.game.common.context.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;
import com.tny.game.net.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class EndpointWorkerMessageCommandExecutor extends ForkJoinMessageCommandExecutor {

    private static final AttrKey<ChildExecutor> COMMAND_CHILD_EXECUTOR = AttrKeys.key(Session.class + "COMMAND_CHILD_EXECUTOR");

    private static final Logger LOG_NET = LoggerFactory.getLogger(NetLogger.EXECUTOR);

    private static Queue<ChildExecutor> scheduledChildExecutors = new ConcurrentLinkedQueue<>();

    public EndpointWorkerMessageCommandExecutor() {
        super();
    }

    public EndpointWorkerMessageCommandExecutor(int threads) {
        super(threads);
    }

    public EndpointWorkerMessageCommandExecutor(int threads, int childDelayTime) {
        super(threads, childDelayTime);
    }

    @Override
    public void submit(NetTunnel<?> tunnel, Command command) {
        Endpoint<?> endpoint = tunnel.getEndpoint();
        ChildExecutor executor = endpoint.attributes().getAttribute(COMMAND_CHILD_EXECUTOR);
        if (executor == null) {
            executor = new ChildExecutor(this.executorService);
            tunnel.attributes().setAttribute(COMMAND_CHILD_EXECUTOR, executor);
            tunnel.attributes().setAttribute(NetAttrKeys.USER_COMMAND_BOX, executor);
        }
        executor.accept(command);
    }

    @Override
    public void prepareStart() {
        super.prepareStart();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            while (!scheduledChildExecutors.isEmpty()) {
                ChildExecutor executor = scheduledChildExecutors.poll();
                if (executor != null)
                    executor.submitWhenDelay();
            }
        }, nextInterval, nextInterval, TimeUnit.MILLISECONDS);
    }

    private static class ChildExecutor implements MessageCommandBox, Runnable {

        /* executor停止 */
        public static final int STOP = 0;
        /* executor提交 */
        public static final int SUBMIT = 1;
        /* executor未完成延迟 */
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
            if (this.state.get() == DELAY && this.state.compareAndSet(DELAY, SUBMIT)) {
                this.executorService.submit(this);
            }
        }

        @Override
        public int size() {
            return this.commandQueue.size();
        }

    }
}
