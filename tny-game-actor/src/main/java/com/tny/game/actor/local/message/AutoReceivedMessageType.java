package com.tny.game.actor.local.message;

/**
 * 自动接受消息类型
 */
public enum AutoReceivedMessageType {

	/**
	 * 终止
	 */
	TERMINATED(true),

	/**
	 * 远程终止
	 */
	ADDRESS_TERMINATED(true),

	IDENTIFY

	;

	private final boolean possiblyHarmful;

	AutoReceivedMessageType() {
		this(false);
	}

	AutoReceivedMessageType(boolean possiblyHarmful) {
		this.possiblyHarmful = possiblyHarmful;
	}

}
