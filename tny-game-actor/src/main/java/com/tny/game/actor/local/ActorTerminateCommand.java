package com.tny.game.actor.local;


import com.tny.game.common.result.*;

/**
 * Actor 终止命令
 * Created by Kun Yang on 16/4/28.
 */
class ActorTerminateCommand extends BaseActorCommand<Void> {

    private ActorTerminateMessage message;

    ActorTerminateCommand(ActorCell actorCell, ActorTerminateMessage message) {
        super(actorCell);
        this.message = message;
    }

    @Override
    protected Done<Void> doHandle() {
        this.actorCell.handle(this.message);
        return DoneResults.successNullable(null);
    }

}
