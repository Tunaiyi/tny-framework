package com.tny.game.net.command.executor;

import com.tny.game.common.context.*;
import com.tny.game.common.runtime.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.task.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.net.utils.*;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.tny.game.net.base.NetLogger.*;

public class EndpointMessageCommandExecutor extends ForkJoinMessageCommandExecutor {

    private static final Queue<ScheduledEventBoxTask> SCHEDULED_EVENT_BOX_TASK_QUEUE = new ConcurrentLinkedQueue<>();

    private static final AttrKey<ChildExecutor> COMMAND_CHILD_EXECUTOR = AttrKeys.key(Session.class + "COMMAND_CHILD_EXECUTOR");

    private static final Logger LOG_NET = LoggerFactory.getLogger(NetLogger.EXECUTOR);

    private static final Queue<ChildExecutor> SCHEDULED_CHILD_EXECUTORS = new ConcurrentLinkedQueue<>();

    public EndpointMessageCommandExecutor() {
        super();
    }

    public EndpointMessageCommandExecutor(int threads) {
        super(threads);
    }

    public EndpointMessageCommandExecutor(int threads, int childDelayTime) {
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
    public void submit(EndpointCommandTaskBox box) {
        this.executorService.submit(box::run);
    }

    @Override
    public void schedule(EndpointCommandTaskBox box, Consumer<EndpointCommandTaskBox> handler) {
        SCHEDULED_EVENT_BOX_TASK_QUEUE.add(new ScheduledEventBoxTask(box, handler));
    }

    @Override
    public boolean isShutdown() {
        return this.executorService.isShutdown();
    }

    @Override
    public void prepareStart() {
        super.prepareStart();
        scheduledExecutor().scheduleAtFixedRate(() -> {
            while (!SCHEDULED_CHILD_EXECUTORS.isEmpty()) {
                ChildExecutor executor = SCHEDULED_CHILD_EXECUTORS.poll();
                if (executor != null) {
                    executor.submitWhenDelay();
                }
            }
            while (!SCHEDULED_EVENT_BOX_TASK_QUEUE.isEmpty()) {
                ScheduledEventBoxTask box = SCHEDULED_EVENT_BOX_TASK_QUEUE.poll();
                if (box != null) {
                    box.execute();
                }
            }
        }, this.nextInterval, this.nextInterval, TimeUnit.MILLISECONDS);
    }

    private static class ScheduledEventBoxTask {

        private final EndpointCommandTaskBox box;
        private final Consumer<EndpointCommandTaskBox> handler;

        private ScheduledEventBoxTask(EndpointCommandTaskBox box,
                Consumer<EndpointCommandTaskBox> handler) {
            this.box = box;
            this.handler = handler;
        }

        public void execute() {
            this.handler.accept(this.box);
        }

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

        private final AtomicInteger state = new AtomicInteger(STOP);

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
                if (!command.isDone()) {
                    this.commandQueue.offer(command);
                }
            } else {
                this.commandQueue.offer(command);
                this.trySubmit();
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
                    Message message = null;
                    if (cmd instanceof MessageCommand) {
                        message = ((MessageCommand<?>)cmd).getMessage();
                        traceDone(NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR, message);
                        traceDone(MSG_DISPATCH_TO_EXECUTE_ATTR, message);
                    }
                    ProcessTracer trace = NET_TRACE_INPUT_EXECUTE_COMMAND_WATCHER.trace();
                    try {
                        cmd.execute();
                    } catch (Throwable e) {
                        LOG_NET.error("run command task {} exception", cmd.getName(), e);
                    }
                    if (cmd.isDone()) {
                        this.commandQueue.poll();
                    } else {
                        this.state.set(DELAY);
                        SCHEDULED_CHILD_EXECUTORS.add(this);
                        break;
                    }
                    trace.done();
                    traceDone(NET_TRACE_INPUT_ALL_ATTR, message);
                } else {
                    this.state.set(STOP);
                    this.submitWhenStop();
                    break;
                }
            }
            this.thread = null;
        }

        private void trySubmit() {
            int currentState = this.state.get();
            if (currentState != SUBMIT && !this.commandQueue.isEmpty() && this.state.compareAndSet(currentState, SUBMIT)) {
                this.executorService.submit(this);
            }
        }

        private void submitWhenStop() {
            if (!this.commandQueue.isEmpty() && this.state.compareAndSet(STOP, SUBMIT)) {
                this.executorService.execute(this);
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
