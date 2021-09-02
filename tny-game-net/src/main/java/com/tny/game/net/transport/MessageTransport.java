package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 2:54 下午
 */
public interface MessageTransport extends Connection {

	/**
	 * 发送消息
	 *
	 * @param message 消息
	 * @param promise 写出等待对象
	 * @return 返回promise
	 * @throws NetException 写出异常
	 */
	WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException;

	/**
	 * 发送消息
	 *
	 * @param maker   消息创建器
	 * @param factory 消息消息工厂
	 * @param context 消息上下文
	 * @return 返回promise
	 * @throws NetException 写出异常
	 */
	WriteMessageFuture write(MessageAllocator maker, MessageFactory factory, MessageContext context) throws NetException;

}
