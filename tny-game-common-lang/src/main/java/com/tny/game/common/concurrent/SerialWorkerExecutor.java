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
package com.tny.game.common.concurrent;

import org.slf4j.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/9/2 20:37
 **/
public abstract class SerialWorkerExecutor implements WorkerExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialWorkerExecutor.class);

    private final Queue<Runnable> taskQueue;

    private ReadWriteLock lock;

    private final AtomicBoolean inLoop = new AtomicBoolean();

    private final Executor masterExecutor;

    private final boolean immediatelyInExecutor;

    private final static ThreadLocal<WorkerExecutor> EXECUTOR_THREAD_LOCAL = new ThreadLocal<>();

    public SerialWorkerExecutor(Executor masterExecutor) {
        this(masterExecutor, new ConcurrentLinkedQueue<>(), true, false);
    }

    public SerialWorkerExecutor(Executor masterExecutor, boolean immediatelyInExecutor) {
        this(masterExecutor, new ConcurrentLinkedQueue<>(), immediatelyInExecutor, false);
    }

    public SerialWorkerExecutor(Executor masterExecutor, Queue<Runnable> taskQueue, boolean unsafeQueue) {
        this(masterExecutor, taskQueue, true, unsafeQueue);
    }

    public SerialWorkerExecutor(Executor masterExecutor, Queue<Runnable> taskQueue, boolean immediatelyInExecutor, boolean unsafeQueue) {
        this.masterExecutor = masterExecutor;
        this.immediatelyInExecutor = immediatelyInExecutor;
        this.taskQueue = taskQueue;
        if (unsafeQueue) {
            this.lock = new ReentrantReadWriteLock();
        }
    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        if (immediatelyInExecutor) {
            var current = EXECUTOR_THREAD_LOCAL.get();
            if (Objects.equals(this, current)) {
                try {
                    runnable.run();
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
                return;
            }
        }
        this.writeLock();
        try {
            this.taskQueue.add(runnable);
        } finally {
            this.writeUnlock();
            this.tryLoop();
        }
    }

    private boolean isQueueEmpty() {
        this.readLock();
        try {
            return taskQueue.isEmpty();
        } finally {
            this.readUnlock();
        }
    }

    private void loop() {
        try {
            EXECUTOR_THREAD_LOCAL.set(this);
            while (true) {
                try {
                    Runnable runnable;
                    this.writeLock();
                    try {
                        if (taskQueue.isEmpty()) {
                            break;
                        }
                        runnable = taskQueue.poll();
                    } finally {
                        this.writeUnlock();
                    }
                    if (runnable == null) {
                        break;
                    }
                    runnable.run();
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        } finally {
            EXECUTOR_THREAD_LOCAL.remove();
            inLoop.set(false);
            tryLoop();
        }
    }

    private void tryLoop() {
        if (!isQueueEmpty() && inLoop.compareAndSet(false, true)) {
            masterExecutor.execute(this::loop);
        }
    }

    private void writeLock() {
        if (lock != null) {
            lock.writeLock().lock();
        }
    }

    private void writeUnlock() {
        if (lock != null) {
            lock.writeLock().unlock();
        }
    }

    private void readLock() {
        if (lock != null) {
            lock.readLock().lock();
        }
    }

    private void readUnlock() {
        if (lock != null) {
            lock.readLock().unlock();
        }
    }

}
