package com.tny.game.actor.local;


import com.tny.game.actor.Actor;
import com.tny.game.actor.stage.TypeTaskStage;
import com.tny.game.common.utils.DoneUtils;
import com.tny.game.common.utils.Done;

/**
 * Actor Message 命令
 * Created by Kun Yang on 16/4/26.
 */
@SuppressWarnings("unchecked")
class ActorMailCommand<T, A extends BaseAnswer<T, TypeTaskStage<T>>> extends ActorAnswerCommand<T, TypeTaskStage<T>, A> implements ActorMail<Object> {

    private Object message;

    private Actor<?, ?> sender;

    ActorMailCommand(ActorCell actorCell, Object message, Actor<?, ?> sender) {
        this(actorCell, message, sender, null);
    }

    ActorMailCommand(ActorCell actorCell, Object message, Actor<?, ?> sender, A answer) {
        super(actorCell, answer);
        this.message = message;
        this.sender = sender;
    }

    @Override
    protected Done<Object> doHandle() {
        return DoneUtils.succNullable(actorCell.handle(this));
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
