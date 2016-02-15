package com.tny.game.actor.system;

import com.tny.game.actor.ActorRef;

/**
 * Actor处理消息失败的系统事件
 * @author KGTny
 *
 */
public class FailedSysMsg extends BaseSystemMessage {

	/**
	 * 子Actor
	 */
	private ActorRef child;
	/**
	 * 失败原因
	 */
	private Throwable cause;

	/**
	 *
	 */
	private long aid;

	public static FailedSysMsg message(ActorRef child, Throwable cause, long aid) {
		return new FailedSysMsg(child, cause, aid);
	}

	private FailedSysMsg(ActorRef child, Throwable cause, long aid) {
		super(SystemMessageType.FAILED);
		this.child = child;
		this.cause = cause;
		this.aid = aid;
	}

	public ActorRef getChild() {
		return child;
	}

	public Throwable getCause() {
		return cause;
	}

	public long getAid() {
		return aid;
	}

}
