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
        if (worker != null && worker.isOnCurrentThread()) {
            command.execute();
        }
        if (!command.isDone()) {
            this.queue.add(command);
        }
        return true;
    }


    @Override
    protected boolean bindWorker(CommandWorker worker) {
        if (this.worker != null)
            return false;
        synchronized (this) {
            if (this.worker != null)
                return false;
            this.preBind();
            this.worker = worker;
            this.postBind();
            for (CommandBox box : commandBoxList)
                box.bindWorker(worker);
            return true;
        }
    }

    @Override
    protected boolean unbindWorker() {
        CommandWorker worker = this.worker;
        if (worker == null)
            return false;
        synchronized (this) {
            if (this.worker != worker)
                return false;
            this.preUnbind();
            this.worker = null;
            this.postUnbind();
            for (CommandBox box : commandBoxList)
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
