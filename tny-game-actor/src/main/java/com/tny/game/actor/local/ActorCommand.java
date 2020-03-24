package com.tny.game.actor.local;

import com.tny.game.actor.*;
import com.tny.game.common.result.*;
import com.tny.game.common.worker.command.*;

/**
 * Actor命令
 * Created by Kun Yang on 16/4/26.
 */
@SuppressWarnings("unchecked")
public abstract class ActorCommand<T> implements Command {

    protected ActorCell actorCell;

    protected ActorCommand(ActorCell actorCell) {
        this.actorCell = actorCell;
    }

    @SuppressWarnings("unchecked")
    protected abstract void handle() throws Throwable;

    @SuppressWarnings("unchecked")
    public abstract T getResult();

    public abstract Answer<T> getAnswer();

    public Done<T> getDone() {
        if (this.isDone())
            return DoneResults.failure();
        return DoneResults.successNullable(this.getResult());
    }

    @Override
    public void execute() {
        if (isDone())
            return;
        this.actorCell.execute(this);
    }

    @Override
    public abstract boolean isDone();

    protected abstract boolean cancel();

    protected abstract void setAnswer(ActorAnswer<T> answer);

}
