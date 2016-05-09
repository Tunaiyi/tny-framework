package com.tny.game.actor.exception;

import com.tny.game.actor.local.ActorCommand;

/**
 * actor Command 异常
 * Created by Kun Yang on 16/1/17.
 */
public abstract class ActorCommandException extends ActorException {

    private ActorCommand<?, ?, ?> command;

    public ActorCommandException(ActorCommand<?, ?, ?> command) {
        this.command = command;
    }

    public ActorCommandException(ActorCommand<?, ?, ?> command, Throwable cause) {
        super(cause);
        this.command = command;
    }

    public ActorCommandException(ActorCommand<?, ?, ?> command, String message, Throwable cause) {
        super(message, cause);
        this.command = command;
    }

    public ActorCommand<?, ?, ?> getCommand() {
        return command;
    }
}
