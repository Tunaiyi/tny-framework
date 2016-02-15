package com.tny.game.actor.exception;

import com.tny.game.actor.ActorRef;

/**
 * Actor初始化异常
 * @author KGTny
 *
 */
public class ActorInitializationException extends ActorException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 相关的ActorRef
	 */
	private ActorRef actorRef;

	public ActorInitializationException(ActorRef actorRef, String message, Throwable cause) {
		super(message, cause);
		this.actorRef = actorRef;
	}

	/**
	 * 获取相关ActorRef
	 * @return ActorRef
	 */
	public ActorRef getActorRef() {
		return actorRef;
	}

}
