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
public interface Transport {

	/**
	 * 写出消息
	 *
	 * @param message 发送消息
	 * @param promise 发送promise
	 */
	WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException;

	/**
	 * 写出消息
	 *
	 * @param context 发送消息
	 */
	WriteMessageFuture write(MessageAllocator allocator, MessageContext context) throws NetException;

	/**
	 * 创建写出Promise
	 *
	 * @return 返回Promise
	 */
	WriteMessagePromise createWritePromise();

}
