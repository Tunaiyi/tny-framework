package com.tny.game.net.endpoint;

import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/12 8:27 下午
 */
public interface MessageAllocator {

	/**
	 * 分配生成消息
	 *
	 * @param factory 消息工厂
	 * @param context 发送上下文
	 * @return 返回消息
	 */
	Message allocate(MessageFactory factory, MessageContext context);

}
