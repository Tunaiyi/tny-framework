package com.tny.game.actor.local;


import com.tny.game.actor.stage.TypeTaskStage;
import com.tny.game.common.utils.DoneUtils;
import com.tny.game.common.utils.Done;

import java.util.concurrent.Callable;

/**
 * Actor Callable 命令
 * Created by Kun Yang on 16/4/26.
 */
class ActorCallCommand<T, A extends BaseAnswer<T, TypeTaskStage<T>>> extends ActorAnswerCommand<T, TypeTaskStage<T>, A> {

    private Callable<T> callable;

    protected ActorCallCommand(ActorCell actorCell, Callable<T> callable, TypeTaskStage<T> stage) {
        this(actorCell, callable, null, stage);
    }

    protected ActorCallCommand(ActorCell actorCell, Callable<T> callable, A answer, TypeTaskStage<T> stage) {
        super(actorCell, answer, stage);
        this.callable = callable;
    }

    @Override
    protected Done<Object> doHandle() throws Exception {
        return DoneUtils.succNullable(this.callable.call());
    }

}
