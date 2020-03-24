package com.tny.game.actor.local;


import com.tny.game.common.result.*;

import java.util.function.Function;

/**
 * Actor Callable 命令
 * Created by Kun Yang on 16/4/26.
 */
class ActorCallUntilCommand<T> extends BaseActorCommand<T> {

    private Function<LocalActor, Done<T>> function;

    protected ActorCallUntilCommand(ActorCell actorCell, Function<LocalActor, Done<T>> function) {
        this(actorCell, function, null);
    }

    protected ActorCallUntilCommand(ActorCell actorCell, Function<LocalActor, Done<T>> function, ActorAnswer<T> answer) {
        super(actorCell, answer);
        this.function = function;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Done<T> doHandle() throws Exception {
        return this.function.apply(this.actorCell.getActor());
    }

}
