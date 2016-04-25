package com.tny.game.worker;

import com.tny.game.worker.command.Command;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleWorkerCommandBox extends AbstractWorkerCommandBox {

    public SimpleWorkerCommandBox(Queue<Command<?>> queue) {
        super(queue);
    }

    public SimpleWorkerCommandBox() {
        super(new ConcurrentLinkedQueue<>());
    }

    @Override
    protected Queue<Command<?>> acceptQueue() {
        return queue;
    }

    public void run() {
        Queue<Command<?>> queue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        runSize = 0;
        for (Command<?> task : queue) {
            if (!task.isDone()) {
                queue.remove(task);
                continue;
            }
            task.execute();
            runSize++;
            if (!task.isCompleted()) {
                continue;
            } else {
                queue.remove(task);
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
