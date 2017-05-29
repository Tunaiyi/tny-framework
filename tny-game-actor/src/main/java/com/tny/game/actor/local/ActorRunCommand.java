package com.tny.game.actor.local;


import com.tny.game.actor.stage.VoidStage;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;

/**
 * Actor Runnable 命令
 * Created by Kun Yang on 16/4/26.
 */
public class ActorRunCommand<A extends BaseAnswer<Void, VoidStage>> extends ActorAnswerCommand<Void, VoidStage, A> {

    private Runnable runnable;

    ActorRunCommand(ActorCell actorCell, Runnable runnable, VoidStage stage) {
        this(actorCell, runnable, null, stage);
    }

    ActorRunCommand(ActorCell actorCell, Runnable runnable, A answer, VoidStage stage) {
        super(actorCell, answer, stage);
        this.runnable = runnable;
    }

    @Override
    protected Done<Object> doHandle() {
        this.runnable.run();
        return DoneUtils.succNullable(null);
    }

}
