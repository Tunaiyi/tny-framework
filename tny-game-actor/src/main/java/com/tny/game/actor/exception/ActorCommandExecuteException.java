package com.tny.game.actor.exception;


import com.tny.game.actor.local.*;

/**
 * actor线程中断异常
 * Created by Kun Yang on 16/1/17.
 */
public class ActorCommandExecuteException extends ActorCommandException {

    public ActorCommandExecuteException(ActorCommand<?> command) {
        super(command);
    }

    public ActorCommandExecuteException(ActorCommand<?> command, Throwable cause) {
        super(command, cause);
    }

    public ActorCommandExecuteException(ActorCommand<?> command, String message, Throwable cause) {
        super(command, message, cause);
    }
}
