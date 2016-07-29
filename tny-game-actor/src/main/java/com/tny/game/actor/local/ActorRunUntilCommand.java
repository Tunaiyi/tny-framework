package com.tny.game.actor.local;


import com.tny.game.actor.stage.VoidTaskStage;
import com.tny.game.common.utils.DoneUtils;
import com.tny.game.common.utils.Done;

import java.util.function.Predicate;

/**
 * Actor Runnable 命令
 * Created by Kun Yang on 16/4/26.
 */
public class ActorRunUntilCommand<A extends BaseAnswer<Void, VoidTaskStage>> extends ActorAnswerCommand<Void, VoidTaskStage, A> {

    private Predicate<LocalActor> predicate;

    ActorRunUntilCommand(ActorCell actorCell, Predicate<LocalActor> predicate, VoidTaskStage stage) {
        this(actorCell, predicate, null, stage);
    }

    ActorRunUntilCommand(ActorCell actorCell, Predicate<LocalActor> predicate, A answer, VoidTaskStage stage) {
        super(actorCell, answer, stage);
        this.predicate = predicate;
    }

    @Override
    protected Done<Object> doHandle() {
        if (this.predicate.test(actorCell.getActor()))
            return DoneUtils.succNullable(null);
        else
            return DoneUtils.fail();
    }

}
