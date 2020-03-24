package com.tny.game.actor.local;


import com.tny.game.common.result.*;

import java.util.concurrent.Callable;

/**
 * Actor Callable 命令
 * Created by Kun Yang on 16/4/26.
 */
class ActorCallCommand<T> extends BaseActorCommand<T> {

    private Callable<T> callable;

    protected ActorCallCommand(ActorCell actorCell, Callable<T> callable) {
        this(actorCell, callable, null);
    }

    protected ActorCallCommand(ActorCell actorCell, Callable<T> callable, ActorAnswer<T> answer) {
        super(actorCell, answer);
        this.callable = callable;
    }

    @Override
    protected Done<T> doHandle() throws Exception {
        return DoneResults.successNullable(this.callable.call());
    }

}
