package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 17:45
 */
public interface MessageTransporter extends Connection {

	/**
	 * 通道通道
	 *
	 * @param tunnel 通道通道
	 */
	void bind(NetTunnel<?> tunnel);

	/**
	 * 发送消息
	 *
	 * @param message 消息
	 * @param awaiter 写出等待对象
	 * @return 返回promise
	 * @throws NetException 写出异常
	 */
	MessageWriteAwaiter write(Message message, MessageWriteAwaiter awaiter) throws NetException;

	/**
	 * 发送消息
	 *
	 * @param maker   消息创建器
	 * @param factory 消息消息工厂
	 * @param context 消息上下文
	 * @return 返回promise
	 * @throws NetException 写出异常
	 */
	MessageWriteAwaiter write(MessageAllocator maker, MessageFactory factory, MessageContext context) throws NetException;

}
