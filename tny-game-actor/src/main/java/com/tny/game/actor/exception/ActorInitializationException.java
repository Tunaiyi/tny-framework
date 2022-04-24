package com.tny.game.actor.exception;

import com.tny.game.actor.*;

/**
 * Actor初始化异常
 *
 * @author KGTny
 */
public class ActorInitializationException extends ActorException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 相关的ActorRef
     */
    private Actor<?, ?> actor;

    public ActorInitializationException(Actor<?, ?> actor, String message, Throwable cause) {
        super(message, cause);
        this.actor = actor;
    }

    /**
     * 获取相关ActorRef
     *
     * @return ActorRef
     */
    public Actor<?, ?> getActorRef() {
        return actor;
    }

}
