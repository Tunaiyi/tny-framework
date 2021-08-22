package com.tny.game.relay.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 4:47 下午
 */
public interface AccessorRelayTunnel<UID> extends RelayTunnel<UID> {

	/**
	 * 关联转发连接
	 *
	 * @param link 关联转发
	 */
	void link(RelayLink link);

	/**
	 * 把 message 转发到 tunnel 绑定的目标
	 *
	 * @param message 消息
	 * @param promise 发送应答对象
	 * @return 返回等待对象
	 */
	WriteMessageFuture relay(Message message, boolean promise);

	/**
	 * 把 context 转发到 tunnel 绑定的目标
	 *
	 * @param allocator 消息配置器
	 * @param factory   消息工厂
	 * @param context   发送上下文
	 * @return 返回等待对象
	 */
	WriteMessageFuture relay(MessageAllocator allocator, MessageFactory factory, MessageContext context);

}