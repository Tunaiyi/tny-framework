package com.tny.game.actor.local;


import com.tny.game.common.utils.*;

import java.util.function.Predicate;

/**
 * Actor Runnable 命令
 * Created by Kun Yang on 16/4/26.
 */
public class ActorRunUntilCommand extends BaseActorCommand<Void> {

    private Predicate<LocalActor> predicate;

    ActorRunUntilCommand(ActorCell actorCell, Predicate<LocalActor> predicate) {
        this(actorCell, predicate, null);
    }

    ActorRunUntilCommand(ActorCell actorCell, Predicate<LocalActor> predicate, ActorAnswer<Void> answer) {
        super(actorCell, answer);
        this.predicate = predicate;
    }

    @Override
    protected Done<Void> doHandle() {
        if (this.predicate.test(actorCell.getActor()))
            return DoneResults.successNullable(null);
        else
            return DoneResults.failure();
    }

}
