package com.tny.game.worker;

import com.tny.game.worker.command.Command;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultWorkerCommandBox<C extends Command, CB extends CommandBox>  extends AbstractWorkerCommandBox<C, CB> {

    public DefaultWorkerCommandBox(Queue<C> queue) {
        super(queue);
    }

    public DefaultWorkerCommandBox() {
        super(new ConcurrentLinkedQueue<>());
    }

    @Override
    protected Queue<C> acceptQueue() {
        return queue;
    }

    public void process() {
        Queue<C> queue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        int currentSize = queue.size();
        runSize = 0;
        for (C cmd : queue) {
            currentSize++;
            if (runSize > currentSize)
                break;
            if (!cmd.isWork()) {
                queue.remove(cmd);
                continue;
            }
            executeCommand(cmd);
            runSize++;
            if (!cmd.isDone()) {
                continue;
            } else {
                queue.remove(cmd);
            }
        }
        for (CommandBox commandBox : boxes()) {
            commandBox.process();
            runSize += commandBox.getProcessSize();
        }
        long finishTime = System.currentTimeMillis();
        runUseTime = finishTime - startTime;
    }

}
