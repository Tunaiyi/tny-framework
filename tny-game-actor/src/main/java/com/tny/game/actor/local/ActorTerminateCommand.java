package com.tny.game.actor.local;


import com.tny.game.actor.Answer;
import com.tny.game.actor.stage.VoidStage;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;

/**
 * Actor 终止命令
 * Created by Kun Yang on 16/4/28.
 */
class ActorTerminateCommand extends ActorCommand<Void, VoidStage, Answer<Void>> {

    private ActorTerminateMessage message;

    ActorTerminateCommand(ActorCell actorCell, ActorTerminateMessage message) {
        super(actorCell);
        this.message = message;
    }

    @Override
    protected Done<Object> doHandle() {
        return DoneUtils.succNullable(actorCell.handle(this.message));
    }

}
