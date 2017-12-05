package com.tny.game.actor.local;


import com.tny.game.actor.Actor;
import com.tny.game.suite.base.Done;
import com.tny.game.suite.base.DoneResults;

/**
 * Actor Message 命令
 * Created by Kun Yang on 16/4/26.
 */
@SuppressWarnings("unchecked")
class ActorMailCommand<T> extends BaseActorCommand<T> implements ActorMail<Object> {

    private Object message;

    private Actor<?, ?> sender;

    ActorMailCommand(ActorCell actorCell, Object message, Actor<?, ?> sender) {
        this(actorCell, message, sender, null);
    }

    ActorMailCommand(ActorCell actorCell, Object message, Actor<?, ?> sender, ActorAnswer<T> answer) {
        super(actorCell, answer);
        this.message = message;
        this.sender = sender;
    }

    @Override
    protected Done<T> doHandle() {
        return DoneResults.succNullable((T) actorCell.handle(this));
    }

    @Override
    public Object getMessage() {
        return this.message;
    }

    @Override
    public <ACT extends Actor> ACT getSender() {
        return (ACT) this.sender;
    }

}
