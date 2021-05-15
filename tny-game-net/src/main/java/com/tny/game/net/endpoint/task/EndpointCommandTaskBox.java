package com.tny.game.net.endpoint.task;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.executor.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:57 下午
 */
public class EndpointCommandTaskBox {

    public static final Logger LOGGER = getLogger(EndpointCommandTaskBox.class);

    private static final Logger LOG_NET = getLogger(NetLogger.EXECUTOR);

    /* executor停止 */
    private static final int STOP = 0;
    /* executor提交 */
    private static final int SUBMIT = 1;
    /* executor未完成延迟 */
    private static final int DELAY = 2;

    private final StampedLock lock = new StampedLock();

    private final MessageCommandExecutor commandExecutor;

    private volatile Queue<EndpointCommandTask> taskQueue = new ConcurrentLinkedQueue<>();

    private volatile Command currentCommand;

    private volatile boolean closed = false;

    private final AtomicInteger processStatus = new AtomicInteger(STOP);

    public EndpointCommandTaskBox(MessageCommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public boolean addTask(EndpointCommandTask task) {
        if (this.closed) {
            return false;
        }
        return StampedLockAide.supplyInOptimisticReadLock(this.lock,
                () -> this.addAndSubmit(task));
    }

    public boolean addTask(Runnable runnable) {
        if (this.closed) {
            return false;
        }
        return StampedLockAide.supplyInOptimisticReadLock(this.lock,
                () -> this.addAndSubmit(new EndpointRunnableCommandTask(runnable)));
    }

    public boolean takeOver(EndpointCommandTaskBox box) {
        if (this.closed) {
            return false;
        }
        return StampedLockAide.supplyInWriteLock(this.lock,
                () -> {
                    if (this.closed) {
                        return false;
                    }
                    Optional<Queue<EndpointCommandTask>> optional = box.close();
                    if (!optional.isPresent()) {
                        return false;
                    }
                    Queue<EndpointCommandTask> queue = optional.get();
                    this.taskQueue.addAll(queue);
                    return true;
                });
    }

    private Optional<Queue<EndpointCommandTask>> close() {
        if (this.closed) {
            return Optional.empty();
        }
        long stamp = this.lock.writeLock();
        try {
            if (this.closed) {
                return Optional.empty();
            }
            this.closed = true;
            Queue<EndpointCommandTask> taskQueue = this.taskQueue;
            this.taskQueue = new ConcurrentLinkedQueue<>();
            return Optional.ofNullable(taskQueue);
        } finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public void run() {
        while (true) {
            if (isCommandRunning()) {
                if (!doCommand(this.currentCommand)) {
                    this.processStatus.set(DELAY);
                    this.commandExecutor.schedule(this, EndpointCommandTaskBox::submitWhenDelay);
                    break;
                } else {
                    this.currentCommand = null;
                }
            }
            // 确保 command 执行完
            if (this.closed) {
                return;
            }
            EndpointCommandTask event = this.taskQueue.poll();
            if (event != null) {
                this.currentCommand = event.createCommand();
            } else {
                this.processStatus.set(STOP);
                this.submitWhenStop();
                break;
            }
        }
    }

    private boolean isCommandRunning() {
        return this.currentCommand != null && this.currentCommand.isDone();
    }

    private boolean addAndSubmit(EndpointCommandTask task) {
        if (this.closed) {
            return false;
        }
        this.taskQueue.add(task);
        int currentState = this.processStatus.get();
        if (currentState != SUBMIT && !this.taskQueue.isEmpty() && this.processStatus.compareAndSet(currentState, STOP)) {
            this.commandExecutor.submit(this);
        }
        return true;
    }

    private void submitWhenDelay() {
        if (this.processStatus.get() == DELAY && this.processStatus.compareAndSet(DELAY, SUBMIT)) {
            this.commandExecutor.submit(this);
        }
    }

    private void submitWhenStop() {
        if (!this.taskQueue.isEmpty() && this.processStatus.compareAndSet(STOP, SUBMIT)) {
            this.commandExecutor.submit(this);
        }
    }

    private boolean doCommand(Command command) {
        try {
            command.execute();
        } catch (Throwable e) {
            LOG_NET.error("run command task {} exception", command.getName(), e);
        }
        return command.isDone();
    }

}
