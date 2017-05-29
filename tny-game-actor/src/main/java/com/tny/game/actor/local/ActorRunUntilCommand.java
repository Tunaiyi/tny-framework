package com.tny.game.actor.local;


import com.tny.game.actor.stage.VoidStage;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;

import java.util.function.Predicate;

/**
 * Actor Runnable 命令
 * Created by Kun Yang on 16/4/26.
 */
public class ActorRunUntilCommand<A extends BaseAnswer<Void, VoidStage>> extends ActorAnswerCommand<Void, VoidStage, A> {

    private Predicate<LocalActor> predicate;

    ActorRunUntilCommand(ActorCell actorCell, Predicate<LocalActor> predicate, VoidStage stage) {
        this(actorCell, predicate, null, stage);
    }

    ActorRunUntilCommand(ActorCell actorCell, Predicate<LocalActor> predicate, A answer, VoidStage stage) {
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
