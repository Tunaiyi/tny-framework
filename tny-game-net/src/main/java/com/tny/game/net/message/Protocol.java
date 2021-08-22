package com.tny.game.net.message;

public interface Protocol {

	int PING_PONG_PROTOCOL_NUM = -1;

	/**
	 * @return 协议号
	 */
	int getProtocolId();

	/**
	 * @return 获取信道号
	 */
	int getLine();

	/**
	 * 指定消息是否是属于此协议
	 *
	 * @param head 消息头
	 * @return
	 */
	default boolean isOwn(MessageHead head) {
		return this.getProtocolId() == head.getProtocolId();
	}

	/**
	 * 指定消息是否是属于此协议
	 *
	 * @param subject 消息
	 * @return
	 */
	default boolean isOwn(MessageContent subject) {
		return this.getProtocolId() == subject.getProtocolId();
	}

}
