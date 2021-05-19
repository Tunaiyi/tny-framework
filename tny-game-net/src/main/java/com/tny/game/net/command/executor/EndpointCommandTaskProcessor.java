package com.tny.game.net.command.executor;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.runtime.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.task.*;
import com.tny.game.net.message.*;
import org.slf4j.Logger;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tny.game.net.base.NetLogger.*;
import static com.tny.game.net.endpoint.task.CommandTaskBoxConstants.*;
import static com.tny.game.net.endpoint.task.CommandTaskBoxStatus.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/17 11:27 上午
 */
public class EndpointCommandTaskProcessor extends ForkJoinCommandTaskExecutor implements CommandTaskProcessor {

    private static final Set<BoxProcessor> SCHEDULED_PROCESSORS = new ConcurrentHashSet<>();

    private static final Logger LOG_NET = getLogger(NetLogger.EXECUTOR);

    @Override
    public void prepareStart() {
        super.prepareStart();
        scheduledExecutor().scheduleAtFixedRate(() -> {
            for (BoxProcessor processor : SCHEDULED_PROCESSORS) {
                SCHEDULED_PROCESSORS.remove(processor);
                processor.onScheduled();
            }
        }, this.nextInterval, this.nextInterval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void submit(CommandTaskBox box) {
        BoxProcessor processor = box.getAttachment(this);
        if (processor == null) {
            processor = box.setAttachmentIfNull(this, () -> new BoxProcessor(box));
        }
        processor.trySubmit();
    }

    private class BoxProcessor implements Runnable {

        /**
         * 执行状态
         */
        private final AtomicInteger executeStatus = new AtomicInteger(STOP_VALUE);

        /**
         * 消息盒子
         */
        private final CommandTaskBox taskBox;

        /**
         * 当前任务
         */
        private volatile Command currentCommand;

        private BoxProcessor(CommandTaskBox taskBox) {
            this.taskBox = taskBox;
        }

        @Override
        public void run() {
            this.executeStatus.set(PROCESSING_VALUE);
            while (true) {
                if (isCommandRunning()) {
                    if (!doCommand(this.currentCommand)) {
                        this.executeStatus.set(DELAY_VALUE);
                        SCHEDULED_PROCESSORS.add(this);
                        break;
                    } else {
                        this.currentCommand = null;
                    }
                }
                // 确保 command 执行完
                if (this.taskBox.isClosed()) {
                    return;
                }
                CommandTask event = this.taskBox.poll();
                if (event != null) {
                    this.currentCommand = event.createCommand();
                    if (this.currentCommand instanceof MessageCommand) {
                        Message message = ((MessageCommand<?>)this.currentCommand).getMessage();
                        traceDone(NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR, message);
                    }
                } else {
                    this.executeStatus.set(STOP_VALUE);
                    this.trySubmitWhen(STOP);
                    break;
                }
            }
        }

        private void onScheduled() {
            this.trySubmit();
        }

        private boolean isCanRun() {
            Command command = this.currentCommand;
            return !this.taskBox.isEmpty() || (command != null && !command.isDone());
        }

        private void trySubmit() {
            int currentState = this.executeStatus.get();
            if (currentState != SUBMIT_VALUE && currentState != PROCESSING_VALUE
                    && isCanRun() && this.executeStatus.compareAndSet(currentState, SUBMIT_VALUE)) {
                EndpointCommandTaskProcessor.this.executorService.submit(this);
            }
        }

        private void trySubmitWhen(CommandTaskBoxStatus status) {
            if (isCanRun() && this.executeStatus.get() == status.getId() && this.executeStatus.compareAndSet(status.getId(), SUBMIT.getId())) {
                EndpointCommandTaskProcessor.this.executorService.submit(this);
            }
        }

        private boolean doCommand(Command command) {
            try {
                ProcessTracer trace = NET_TRACE_INPUT_EXECUTE_COMMAND_WATCHER.trace();
                command.execute();
                trace.done();
            } catch (Throwable e) {
                LOG_NET.error("run command task {} exception", command.getName(), e);
            }
            return command.isDone();
        }

        private boolean isCommandRunning() {
            return this.currentCommand != null && !this.currentCommand.isDone();
        }

    }

}
