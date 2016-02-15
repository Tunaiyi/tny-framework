package com.tny.game.actor.exception;

/**
 * actor线程中断异常
 * Created by Kun Yang on 16/1/17.
 */
public class ActorInterruptedException extends ActorException {

    public ActorInterruptedException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
