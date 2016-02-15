package com.tny.game.actor.system;

/**
 * 挂起Actor系统事件
 * @author KGTny
 *
 */
public class SuspendSysMsg extends BaseSystemMessage {

	private static final SuspendSysMsg message = new SuspendSysMsg();

	private SuspendSysMsg() {
		super(SystemMessageType.SUSPEND);
	}

	public static SuspendSysMsg message() {
		return message;
	}

}
