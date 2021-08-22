package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 18:05
 */
public interface Sender {

	/**
	 * 异步发送消息
	 *
	 * @param messageContext 发送消息上下文
	 * @return 返回发送上下文
	 */
	SendContext send(MessageContext messageContext);

}
