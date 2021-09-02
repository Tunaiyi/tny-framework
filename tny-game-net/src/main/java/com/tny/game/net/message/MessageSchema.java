package com.tny.game.net.message;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 2:25 下午
 */
public interface MessageSchema extends Protocol {

	/**
	 * @return 响应消息 -1 为无
	 */
	long getToMessage();

	/**
	 * @return 消息类型
	 */
	default MessageType getType() {
		return getMode().getType();
	}

	/**
	 * @return 获取消息模式
	 */
	MessageMode getMode();

}
