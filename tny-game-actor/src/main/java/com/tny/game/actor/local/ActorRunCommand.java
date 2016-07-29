package com.tny.game.actor.local;


import com.tny.game.actor.stage.VoidTaskStage;
import com.tny.game.common.utils.DoneUtils;
import com.tny.game.common.utils.Done;

/**
 * Actor Runnable 命令
 * Created by Kun Yang on 16/4/26.
 */
public class ActorRunCommand<A extends BaseAnswer<Void, VoidTaskStage>> extends ActorAnswerCommand<Void, VoidTaskStage, A> {

    private Runnable runnable;

    ActorRunCommand(ActorCell actorCell, Runnable runnable, VoidTaskStage stage) {
        this(actorCell, runnable, null, stage);
    }

    ActorRunCommand(ActorCell actorCell, Runnable runnable, A answer, VoidTaskStage stage) {
        super(actorCell, answer, stage);
        this.runnable = runnable;
    }

    @Override
    protected Done<Object> doHandle() {
        this.runnable.run();
        return DoneUtils.succNullable(null);
    }

}
