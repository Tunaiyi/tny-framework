package com.tny.game.actor.local;


import com.tny.game.actor.exception.ActorTerminatedException;
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
public abstract class ActorCommandBox extends AbstractWorkerCommandBox<ActorCommand<?, ?, ?>, ActorCommandBox> implements Runnable {

    private ActorCell actorCell;

    private volatile boolean terminated;

    private AtomicBoolean submit = new AtomicBoolean(false);

    public ActorCommandBox(ActorCell actorCell) {
        super(new ConcurrentLinkedQueue<>());
        this.actorCell = actorCell;
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
    protected void postAcceptIntoQueue(ActorCommand<?, ?, ?> command) {
        CommandWorker worker = this.worker;
        if (worker != null && worker instanceof ActorCommandWorker) {
            ((ActorCommandWorker) worker).trySubmit(this);
        }
    }


    protected void terminate() {
        if (this.terminated)
            return;
        this.terminated = true;
        Queue<ActorCommand<?, ?, ?>> queue = this.acceptQueue();
        while (!queue.isEmpty()) {
            ActorCommand<?, ?, ?> cmd = queue.poll();
            if (!cmd.isWork())
                continue;
            cmd.cancel();
            this.executeCommand(cmd);
        }
        for (ActorCommandBox box : boxes())
            box.getActorCell().terminate();
    }

    boolean isTerminated() {
        return this.terminated;
    }

    boolean detach() {
        return this.worker != null && this.worker.unregister(this);
    }

    private ActorCell getActorCell() {
        return actorCell;
    }

    private void checkTerminated() {
        if (this.isTerminated())
            throw new ActorTerminatedException(this.actorCell.getActor());
    }

    @Override
    public boolean accept(ActorCommand<?, ?, ?> command) {
        this.checkTerminated();
        return super.accept(command);
    }

    @Override
    public boolean bindWorker(CommandWorker worker) {
        return !this.isTerminated() && super.bindWorker(worker);
    }

    @Override
    public boolean unbindWorker() {
        return super.unbindWorker();
    }

    @Override
    public boolean register(CommandBox commandBox) {
        return !this.terminated && super.register(commandBox);
    }


}
