package com.tny.game.actor.system;

/**
 * 终止Actor系统事件
 * @author KGTny
 *
 */
public class TerminateSysMsg extends BaseSystemMessage {

	private static final TerminateSysMsg message = new TerminateSysMsg();

	private TerminateSysMsg() {
		super(SystemMessageType.TERMINATE);
	}

	public static TerminateSysMsg message() {
		return message;
	}

}
