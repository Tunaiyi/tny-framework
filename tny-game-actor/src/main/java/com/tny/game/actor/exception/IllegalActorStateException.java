package com.tny.game.actor.exception;

/**
 * actor状态异常
 * Created by Kun Yang on 16/1/17.
 */
public class IllegalActorStateException extends ActorException {

    public IllegalActorStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalActorStateException(String message) {
        super(message);
    }

}
