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
	 * @param protocol 消息头
	 */
	default boolean isOwn(Protocol protocol) {
		return this.getProtocolId() == protocol.getProtocolId();
	}

}
