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
package com.tny.game.net.command.processor;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.command.dispatcher.*;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

import static com.tny.game.common.utils.ObjectAide.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:57 下午
 */
public class MessageCommandBox implements Executor {

    public static final Logger LOGGER = getLogger(MessageCommandBox.class);

    /**
     * 队列锁
     */
    private final StampedLock queueLock = new StampedLock();

    /**
     * 消息盒子调度器
     */
    private final CommandBoxProcessor processor;

    /**
     * 命令任务队列
     */
    private volatile Queue<Command> commandQueue = new ConcurrentLinkedQueue<>();

    /**
     * 执行任务
     */
    private volatile Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * 是否关闭
     */
    private volatile boolean closed = false;

    /**
     * 附件
     */
    private Object attachment;

    public MessageCommandBox(CommandBoxProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        StampedLockAide.supplyInOptimisticReadLock(this.queueLock, () -> this.doAddRunnable(runnable));
    }

    public boolean addCommand(RpcEnterContext rpcContext) {
        if (this.closed) {
            return false;
        }
        return StampedLockAide.supplyInOptimisticReadLock(this.queueLock, () -> this.doAddTask(rpcContext));
    }

    private Command createCommand(RpcEnterContext rpcContext) {
        var message = rpcContext.getMessage();
        switch (message.getMode()) {
            case PUSH:
            case REQUEST:
            case RESPONSE:
                var context = rpcContext.networkContext();
                MessageDispatcher dispatcher = context.getMessageDispatcher();
                return dispatcher.dispatch(rpcContext);
            case PING:
                var tunnel = rpcContext.netTunnel();
                rpcContext.complete();
                return new RunnableCommand(tunnel::pong);
            default:
        }
        rpcContext.complete();
        return null;
    }

    public boolean takeOver(MessageCommandBox box) {
        if (this.closed) {
            return false;
        }
        if (box == this) {
            return false;
        }
        return StampedLockAide.supplyInWriteLock(this.queueLock,
                () -> {
                    if (this.closed) {
                        return false;
                    }
                    var optional = box.close();
                    if (optional.isEmpty()) {
                        return false;
                    }
                    var result = optional.get();
                    this.commandQueue.addAll(result.getCommandQueue());
                    this.taskQueue.addAll(result.getTaskQueue());
                    return true;
                });
    }

    private Optional<ClosedResult> close() {
        if (this.closed) {
            return Optional.empty();
        }
        return StampedLockAide.supplyInWriteLock(this.queueLock,
                () -> {
                    long stamp = this.queueLock.writeLock();
                    try {
                        if (this.closed) {
                            return Optional.empty();
                        }
                        this.closed = true;
                        var commandQueue = this.commandQueue;
                        var taskQueue = this.taskQueue;
                        this.commandQueue = new ConcurrentLinkedQueue<>();
                        this.taskQueue = new ConcurrentLinkedQueue<>();
                        return Optional.of(new ClosedResult(commandQueue, taskQueue));
                    } finally {
                        this.queueLock.unlockWrite(stamp);
                    }
                });
    }

    public Command pollCommand() {
        return StampedLockAide.supplyInOptimisticReadLock(this.queueLock,
                () -> {
                    if (this.closed) {
                        return null;
                    }
                    return this.commandQueue.poll();
                });
    }

    public Runnable pollExecuteTask() {
        return StampedLockAide.supplyInOptimisticReadLock(this.queueLock,
                () -> {
                    if (this.closed) {
                        return null;
                    }
                    return this.taskQueue.poll();
                });
    }

    public boolean isClosed() {
        return this.closed;
    }

    public boolean isEmpty() {
        return this.commandQueue.isEmpty();
    }

    public <T> T getAttachment(CommandBoxProcessor executor) {
        if (this.processor == executor) {
            return as(this.attachment);
        }
        return null;
    }

    public <T> T setAttachmentIfNull(CommandBoxProcessor executor, Supplier<T> attachmentCreator) {
        if (this.processor == executor) {
            synchronized (this) {
                if (this.attachment == null) {
                    this.attachment = attachmentCreator.get();
                }
            }
            return as(this.attachment);
        }
        return null;
    }

    public <T> T setAttachmentIfNull(CommandBoxProcessor executor, T attachment) {
        if (this.processor == executor) {
            synchronized (this) {
                if (this.attachment == null) {
                    this.attachment = attachment;
                }
            }
            return as(this.attachment);
        }
        return null;
    }

    public <T> T setAttachment(CommandBoxProcessor executor, Supplier<T> attachmentCreator) {
        if (this.processor == executor) {
            synchronized (this) {
                this.attachment = attachmentCreator.get();
                return as(this.attachment);
            }
        }
        return null;
    }

    public <T> T setAttachment(CommandBoxProcessor executor, T attachment) {
        if (this.processor == executor) {
            synchronized (this) {
                this.attachment = attachment;
                return as(this.attachment);
            }
        }
        return null;
    }

    private boolean doAddTask(RpcEnterContext rpcContext) {
        if (this.closed) {
            return false;
        }
        this.commandQueue.add(createCommand(rpcContext));
        this.processor.submit(this);
        return true;
    }

    private boolean doAddRunnable(Runnable runnable) {
        if (this.closed) {
            return false;
        }
        this.taskQueue.add(runnable);
        this.processor.submit(this);
        return true;
    }

    private static class ClosedResult {

        private final Queue<Command> commandQueue;

        private final Queue<Runnable> taskQueue;

        private ClosedResult(Queue<Command> commandQueue, Queue<Runnable> taskQueue) {
            this.commandQueue = commandQueue;
            this.taskQueue = taskQueue;
        }

        private Queue<Command> getCommandQueue() {
            return commandQueue;
        }

        private Queue<Runnable> getTaskQueue() {
            return taskQueue;
        }

    }

}
