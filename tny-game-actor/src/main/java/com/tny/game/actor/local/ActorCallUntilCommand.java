package com.tny.game.actor.local;


import com.tny.game.actor.stage.TypeTaskStage;
import com.tny.game.common.utils.Done;

import java.util.function.Function;

/**
 * Actor Callable 命令
 * Created by Kun Yang on 16/4/26.
 */
class ActorCallUntilCommand<T, A extends BaseAnswer<T, TypeTaskStage<T>>> extends ActorAnswerCommand<T, TypeTaskStage<T>, A> {

    private Function<LocalActor, Done<T>> function;

    protected ActorCallUntilCommand(ActorCell actorCell, Function<LocalActor, Done<T>> function, TypeTaskStage<T> stage) {
        this(actorCell, function, null, stage);
    }

    protected ActorCallUntilCommand(ActorCell actorCell, Function<LocalActor, Done<T>> function, A answer, TypeTaskStage<T> stage) {
        super(actorCell, answer, stage);
        this.function = function;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Done<Object> doHandle() throws Exception {
        return (Done<Object>) this.function.apply(actorCell.getActor());
    }

}
