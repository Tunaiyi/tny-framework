package com.tny.game.worker;

import com.tny.game.worker.command.CommandTask;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleWorkerCommandBox extends AbstractWorkerCommandBox {

    public SimpleWorkerCommandBox(Queue<CommandTask<?>> queue) {
        super(queue);
    }

    public SimpleWorkerCommandBox() {
        super(new ConcurrentLinkedQueue<CommandTask<?>>());
    }

    @Override
    protected Queue<CommandTask<?>> acceptQueue() {
        return queue;
    }

    public void run() {
        Queue<CommandTask<?>> queue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        runSize = 0;
        for (CommandTask<?> task : queue) {
            if (!task.getCommand().isWorking()) {
                task.fail(false);
                queue.remove(task);
                continue;
            }
            if (task.getCommand().isCanExecute()) {
                task.run();
                runSize++;
                if (!task.getCommand().isCompleted()) {
                    continue;
                }
            }
            if (task.getCommand().isCompleted()) {
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
