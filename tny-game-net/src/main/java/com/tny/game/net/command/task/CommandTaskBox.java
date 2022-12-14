/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.command.task;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.command.processor.*;
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
public class CommandTaskBox implements Executor {

    public static final Logger LOGGER = getLogger(CommandTaskBox.class);

    /**
     * 队列锁
     */
    private final StampedLock queueLock = new StampedLock();

    /**
     * 消息盒子调度器
     */
    private final CommandTaskBoxProcessor executor;

    /**
     * 命令任务队列
     */
    private volatile Queue<CommandTask> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * 是否关闭
     */
    private volatile boolean closed = false;

    private Object attachment;

    public CommandTaskBox(CommandTaskBoxProcessor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        this.addTask(command);
    }

    public boolean addTask(CommandTask task) {
        if (this.closed) {
            return false;
        }
        return StampedLockAide.supplyInOptimisticReadLock(this.queueLock,
                () -> this.doAddTask(task));
    }

    private void addTask(Runnable runnable) {
        if (this.closed) {
            return;
        }
        StampedLockAide.supplyInOptimisticReadLock(this.queueLock,
                () -> this.doAddTask(new RunnableCommandTask(runnable)));
    }

    public boolean takeOver(CommandTaskBox box) {
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
                    Optional<Queue<CommandTask>> optional = box.close();
                    if (optional.isEmpty()) {
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

    public <T> T getAttachment(CommandTaskBoxProcessor executor) {
        if (this.executor == executor) {
            return as(this.attachment);
        }
        return null;
    }

    public <T> T setAttachmentIfNull(CommandTaskBoxProcessor executor, Supplier<T> attachmentCreator) {
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

    public <T> T setAttachmentIfNull(CommandTaskBoxProcessor executor, T attachment) {
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

    public <T> T setAttachment(CommandTaskBoxProcessor executor, Supplier<T> attachmentCreator) {
        if (this.executor == executor) {
            synchronized (this) {
                this.attachment = attachmentCreator.get();
                return as(this.attachment);
            }
        }
        return null;
    }

    public <T> T setAttachment(CommandTaskBoxProcessor executor, T attachment) {
        if (this.executor == executor) {
            synchronized (this) {
                this.attachment = attachment;
                return as(this.attachment);
            }
        }
        return null;
    }

    private boolean doAddTask(CommandTask task) {
        if (this.closed) {
            return false;
        }
        this.taskQueue.add(task);
        this.executor.submit(this);
        return true;
    }

}
