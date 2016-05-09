package com.tny.game.actor.exception;

public class ActorException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ActorException() {

    }

    public ActorException(String message) {
        super(message);
    }

    public ActorException(Throwable cause) {
        super(cause);
    }

    public ActorException(String message, Throwable cause) {
        super(message, cause);
    }

}
