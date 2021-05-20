package com.tny.game.net.endpoint.task;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.StampedLock;
import java.util.function.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:57 下午
 */
public class CommandTaskBox {

    public static final Logger LOGGER = getLogger(CommandTaskBox.class);

    /**
     * 队列锁
     */
    private final StampedLock queueLock = new StampedLock();

    /**
     * 消息盒子调度器
     */
    private final CommandTaskProcessor executor;

    private final MessageDispatcher messageDispatcher;

    /**
     * 命令任务队列
     */
    private volatile Queue<CommandTask> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * 是否关闭
     */
    private volatile boolean closed = false;

    private Object attachment;

    public CommandTaskBox(MessageDispatcher messageDispatcher, CommandTaskProcessor executor) {
        this.messageDispatcher = messageDispatcher;
        this.executor = executor;
    }

    public boolean addMessage(NetTunnel<?> tunnel, Message message, Function<Message, RespondFuture> futureGetter) {
        if (this.closed) {
            return false;
        }
        return StampedLockAide.supplyInOptimisticReadLock(this.queueLock,
                () -> {
                    RespondCommandTask<?> respondCommandTask = null;
                    if (futureGetter != null) {
                        RespondFuture future = futureGetter.apply(message);
                        if (future != null) {
                            respondCommandTask = new RespondCommandTask<>(message, future);
                        }
                    }
                    MessageCommandTask<?> messageCommandTask = new MessageCommandTask<>(tunnel, message, this.messageDispatcher);
                    if (respondCommandTask == null) {
                        return this.addAndSubmit(messageCommandTask);
                    } else {
                        return this.addAndSubmit(respondCommandTask, messageCommandTask);
                    }
                });
    }

    public boolean addTask(CommandTask task) {
        if (this.closed) {
            return false;
        }
        return StampedLockAide.supplyInOptimisticReadLock(this.queueLock,
                () -> this.addAndSubmit(task));
    }

    public boolean addTask(Runnable runnable) {
        if (this.closed) {
            return false;
        }
        return StampedLockAide.supplyInOptimisticReadLock(this.queueLock,
                () -> this.addAndSubmit(new RunnableCommandTask(runnable)));
    }

    public boolean takeOver(CommandTaskBox box) {
        if (this.closed) {
            return false;
        }
        return StampedLockAide.supplyInWriteLock(this.queueLock,
                () -> {
                    if (this.closed) {
                        return false;
                    }
                    Optional<Queue<CommandTask>> optional = box.close();
                    if (!optional.isPresent()) {
                        return false;
                    }
                    Queue<CommandTask> queue = optional.get();
                    this.taskQueue.addAll(queue);
                    return true;
                });
    }

    private Optional<Queue<CommandTask>> close() {
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
                        Queue<CommandTask> taskQueue = this.taskQueue;
                        this.taskQueue = new ConcurrentLinkedQueue<>();
                        return Optional.ofNullable(taskQueue);
                    } finally {
                        this.queueLock.unlockWrite(stamp);
                    }
                });
    }

    public CommandTask poll() {
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
        return this.taskQueue.isEmpty();
    }

    public <T> T getAttachment(CommandTaskProcessor executor) {
        if (this.executor == executor) {
            return as(this.attachment);
        }
        return null;
    }

    public <T> T setAttachmentIfNull(CommandTaskProcessor executor, Supplier<T> attachmentCreator) {
        if (this.executor == executor) {
            synchronized (this) {
                if (this.attachment == null) {
                    this.attachment = attachmentCreator.get();
                }
            }
            return as(this.attachment);
        }
        return null;
    }

    public <T> T setAttachmentIfNull(CommandTaskProcessor executor, T attachment) {
        if (this.executor == executor) {
            synchronized (this) {
                if (this.attachment == null) {
                    this.attachment = attachment;
                }
            }
            return as(this.attachment);
        }
        return null;
    }

    public <T> T setAttachment(CommandTaskProcessor executor, T attachment) {
        if (this.executor == executor) {
            synchronized (this) {
                this.attachment = attachment;
                return as(this.attachment);
            }
        }
        return null;
    }

    private boolean addAndSubmit(CommandTask... tasks) {
        if (this.closed) {
            return false;
        }
        for (CommandTask task : tasks) {
            if (task != null) {
                this.taskQueue.add(task);
            }
        }
        this.executor.submit(this);
        return true;
    }

    private boolean addAndSubmit(CommandTask task) {
        if (this.closed) {
            return false;
        }
        this.taskQueue.add(task);
        this.executor.submit(this);
        return true;
    }

}
