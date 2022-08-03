package com.tny.game.common.worker;

import com.tny.game.common.utils.*;
import com.tny.game.common.worker.command.*;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.*;

public class FrequencySwitchQueueCommandBox<C extends Command, CB extends CommandBox<C>> extends AbstractWorkerCommandBox<C, CB> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogAide.WORKER);

    private final Queue<C> fromQueue;

    private final Queue<C> toQueue;

    private final Lock lock = new ReentrantLock();

    public FrequencySwitchQueueCommandBox(Queue<C> fromQueue, Queue<C> toQueue) {
        super(new ConcurrentLinkedQueue<>());
        this.fromQueue = fromQueue;
        this.toQueue = toQueue;
        this.queue = fromQueue;
    }

    public FrequencySwitchQueueCommandBox() {
        this(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
    }

    @Override
    protected Queue<C> acceptQueue() {
        while (true) {
            if (this.lock.tryLock()) {
                Queue<C> accQueue = this.queue;
                this.queue = accQueue != this.toQueue ? this.toQueue : this.fromQueue;
                this.queue = accQueue;
                return accQueue;
            } else {
                Thread.yield();
            }
        }
    }

    @Override
    public void clear() {
        this.queue.clear();
        this.toQueue.clear();
        this.fromQueue.clear();
    }

    @Override
    public void submit() {
    }

    @Override
    public boolean isEmpty() {
        return this.toQueue.isEmpty() && this.fromQueue.isEmpty();
    }

    @Override
    public int size() {
        return this.toQueue.size() + this.fromQueue.size();
    }

    @Override
    protected void doProcess() {
        Queue<C> currentRunQueue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        this.runSize = 0;
        C cmd = currentRunQueue.poll();
        while (cmd != null) {
            executeCommand(cmd);
            this.runSize++;
            if (!cmd.isDone()) {
                this.queue.add(cmd);
            }
            cmd = currentRunQueue.poll();
        }
        for (CommandBox<?> commandBox : boxes()) {
            commandBox.process();
            // commandBox.process();
            // this.worker.submit(commandBox);
            // runSize += commandBox.getProcessSize();
        }
        long finishTime = System.currentTimeMillis();
        this.runUseTime = finishTime - startTime;
    }

    @Override
    public void wakeUp(CommandBox<?> commandBox) {
    }

}
