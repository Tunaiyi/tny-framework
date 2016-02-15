package com.tny.game.actor.system;

/**
 * 无的系统事件
 * @author KGTny
 *
 */
public class NoneSysMsg extends BaseSystemMessage {

	private static final NoneSysMsg message = new NoneSysMsg();

	private NoneSysMsg() {
		super(SystemMessageType.NONE);
	}

	public static NoneSysMsg message() {
		return message;
	}

}
