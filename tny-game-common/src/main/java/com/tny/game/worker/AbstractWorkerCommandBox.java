package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public abstract class AbstractWorkerCommandBox<C extends Command, CB extends CommandBox> extends WorkerCommandBox<C, CB> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    protected volatile Queue<C> queue;

    public AbstractWorkerCommandBox(Queue<C> queue) {
        super();
        this.queue = queue;
    }

    protected abstract Queue<C> acceptQueue();

    protected Queue<C> getQueue() {
        return queue;
    }

    protected boolean executeIfCurrent(C command) {
        CommandWorker worker = this.worker;
        if (worker != null && worker.isOnCurrentThread())
            executeCommand(command);
        if (!command.isDone()) {
            this.queue.add(command);
            postAcceptIntoQueue(command);
        }
        postAccept(command);
        return true;
    }

    protected void postAccept(C command) {
    }

    protected void postAcceptIntoQueue(C command) {

    }

    @Override
    public boolean bindWorker(CommandWorker worker) {
        if (this.worker != null)
            return false;
        synchronized (this) {
            if (this.worker != null)
                return false;
            this.preBind();
            this.worker = worker;
            this.postBind();
            for (CB box : boxes())
                box.bindWorker(worker);
            return true;
        }
    }

    @Override
    public boolean unbindWorker() {
        CommandWorker worker = this.worker;
        if (worker == null)
            return false;
        synchronized (this) {
            if (this.worker != worker)
                return false;
            this.preUnbind();
            this.worker = null;
            this.postUnbind();
            for (CB box : boxes())
                box.unbindWorker();
            return true;
        }
    }

    protected void preBind() {
    }

    protected void preUnbind() {
    }

    protected void postBind() {
    }

    protected void postUnbind() {
    }

    @Override
    public void clear() {
        queue.clear();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    protected void executeCommand(C command) {
        command.execute();
    }

}
