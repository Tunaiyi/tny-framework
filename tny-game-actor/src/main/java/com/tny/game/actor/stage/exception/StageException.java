package com.tny.game.actor.stage.exception;

public class StageException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public StageException(String message) {
        super(message);
    }

    public StageException(Throwable cause) {
        super(cause);
    }

    public StageException(String message, Throwable cause) {
        super(message, cause);
    }

}
