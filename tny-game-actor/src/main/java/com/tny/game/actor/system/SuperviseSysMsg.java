package com.tny.game.actor.system;

import com.tny.game.actor.ActorRef;

/**
 * 请求监护子Actor事件处理状态的系统事件
 * @author KGTny
 *
 */
public class SuperviseSysMsg extends BaseSystemMessage {

	/**
	 * 监控的子Actor
	 */
	private ActorRef child;

	public static SuperviseSysMsg message(ActorRef child) {
		return new SuperviseSysMsg(child);
	}

	private SuperviseSysMsg(ActorRef child) {
		super(SystemMessageType.SUPERVISE);
		this.child = child;
	}

	/**
	 * @return 返回监控的子Actor
	 */
	public ActorRef getChild() {
		return child;
	}

}
