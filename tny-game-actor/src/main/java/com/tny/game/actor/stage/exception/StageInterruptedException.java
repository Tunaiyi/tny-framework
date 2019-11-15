package com.tny.game.actor.stage.exception;

public class StageInterruptedException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public StageInterruptedException(String message) {
        super(message);
    }

    public StageInterruptedException(Throwable cause) {
        super(cause);
    }

    public StageInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

}
