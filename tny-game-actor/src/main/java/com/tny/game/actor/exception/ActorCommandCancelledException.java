package com.tny.game.actor.exception;

import com.tny.game.actor.local.ActorCommand;

/**
 * actor线程中断异常
 * Created by Kun Yang on 16/1/17.
 */
public class ActorCommandCancelledException extends ActorCommandException {

    public ActorCommandCancelledException(ActorCommand<?> command) {
        super(command);
    }

    public ActorCommandCancelledException(ActorCommand<?> command, Throwable cause) {
        super(command, cause);
    }

    public ActorCommandCancelledException(ActorCommand<?> command, String message, Throwable cause) {
        super(command, message, cause);
    }
}
