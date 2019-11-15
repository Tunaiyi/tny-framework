package com.tny.game.actor.exception;

import com.tny.game.actor.*;

/**
 * Actor初始化异常
 *
 * @author KGTny
 */
public class ActorTerminatedException extends ActorException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 相关的Actor
     */
    private Actor<?, ?> actor;

    public ActorTerminatedException(Actor<?, ?> actor) {
        super("", null);
        this.actor = actor;
    }

    public ActorTerminatedException(Actor<?, ?> actor, String message) {
        super(message, null);
        this.actor = actor;
    }

    /**
     * 获取相关Actor
     *
     * @return Actor
     */
    public Actor<?, ?> getActor() {
        return actor;
    }

}
