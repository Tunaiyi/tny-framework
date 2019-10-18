package com.tny.game.actor.local;


import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneResults;

/**
 * Actor Runnable 命令
 * Created by Kun Yang on 16/4/26.
 */
public class ActorRunCommand extends BaseActorCommand<Void> {

    private Runnable runnable;

    ActorRunCommand(ActorCell actorCell, Runnable runnable) {
        this(actorCell, runnable, null);
    }

    ActorRunCommand(ActorCell actorCell, Runnable runnable, ActorAnswer<Void> answer) {
        super(actorCell, answer);
        this.runnable = runnable;
    }

    @Override
    protected Done<Void> doHandle() {
        this.runnable.run();
        return DoneResults.successNullable(null);
    }

}
