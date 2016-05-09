package com.tny.game.actor.local;


import com.tny.game.actor.local.ActorCommandExecutor.ActorCommandWorker;
import com.tny.game.worker.AbstractWorkerCommandBox;
import com.tny.game.worker.CommandBox;
import com.tny.game.worker.CommandWorker;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Actor 命令箱子
 * Created by Kun Yang on 16/4/25.
 */
public abstract class ActorCommandBox<ACB extends ActorCommandBox> extends AbstractWorkerCommandBox<ActorCommand<?, ?, ?>, ACB> implements Runnable {

//    private int handleTimes = 0;

    private AtomicBoolean submit = new AtomicBoolean(false);

    public ActorCommandBox() {
        super(new ConcurrentLinkedQueue<>());
    }

    @Override
    protected Queue<ActorCommand<?, ?, ?>> acceptQueue() {
        return queue;
    }

    boolean trySubmit() {
        return !this.queue.isEmpty() && this.submit.compareAndSet(false, true);
    }

    @Override
    public void run() {
        try {
            this.worker.submit(this);
        } finally {
            this.submit.set(false);
        }
    }

    @Override
    public void process() {
//        System.out.println(++handleTimes);
        ActorCommand<?, ?, ?> delimiter = null;
        Queue<ActorCommand<?, ?, ?>> queue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        this.runSize = 0;
        while (!queue.isEmpty()) {
            ActorCommand<?, ?, ?> cmd = queue.peek();
            if (cmd == delimiter)
                break;
            queue.poll();
            if (!cmd.isWork()) {
                continue;
            }
            this.executeCommand(cmd);
            this.runSize++;
            if (!cmd.isDone()) {
                if (delimiter == null)
                    delimiter = cmd;
                queue.add(cmd);
            }
        }
        for (CommandBox commandBox : boxes()) {
            this.worker.submit(commandBox);
            this.runSize += commandBox.getProcessSize();
        }
        long finishTime = System.currentTimeMillis();
        this.runUseTime = finishTime - startTime;
    }

    @Override
    protected void postAcceptIntoQueue(ActorCommand<?, ?, ?> command) {
        CommandWorker worker = this.worker;
        if (worker != null && worker instanceof ActorCommandWorker) {
            ((ActorCommandWorker) worker).trySubmit(this);
        }
    }

}
