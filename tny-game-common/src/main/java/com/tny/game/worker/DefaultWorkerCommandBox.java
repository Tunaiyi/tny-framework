package com.tny.game.worker;

import com.tny.game.worker.command.Command;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultWorkerCommandBox extends AbstractWorkerCommandBox {

    public DefaultWorkerCommandBox(Queue<Command> queue) {
        super(queue);
    }

    public DefaultWorkerCommandBox() {
        super(new ConcurrentLinkedQueue<>());
    }

    @Override
    protected Queue<Command> acceptQueue() {
        return queue;
    }

    public void run() {
        Queue<Command> queue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        int currentSize = queue.size();
        runSize = 0;
        for (Command cmd : queue) {
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
        for (CommandBox commandBox : commandBoxList) {
            commandBox.run();
            runSize += commandBox.getRunSize();
        }
        long finishTime = System.currentTimeMillis();
        runUseTime = finishTime - startTime;
    }

}
