package com.tny.game.actor.exception;

/**
 * 配置异常
 * Created by Kun Yang on 16/1/17.
 */
public class InvalidActorNameException extends ActorException {

    public InvalidActorNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidActorNameException(String message) {
        super(message);
    }

}
