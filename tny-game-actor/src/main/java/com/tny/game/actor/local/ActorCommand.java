package com.tny.game.actor.local;

import com.tny.game.actor.Answer;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;
import com.tny.game.worker.command.Command;

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
            return DoneUtils.fail();
        return DoneUtils.succNullable(this.getResult());
    }

    @Override
    public void execute() {
        if (isDone())
            return;
        actorCell.execute(this);
    }

    @Override
    public abstract boolean isDone();

    protected abstract boolean cancel();

    protected abstract void setAnswer(ActorAnswer<T> answer);

}
