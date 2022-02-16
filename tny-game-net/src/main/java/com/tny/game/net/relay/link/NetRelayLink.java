package com.tny.game.net.relay.link;

import com.tny.game.common.event.firer.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.listener.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.transport.*;

/**
 * 转发链接
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 11:45 上午
 */
public interface NetRelayLink extends RelayLink, EventSourceObject<RelayLinkListener> {

	static String idOf(NetRelayLink relayLink) {
		return idOf(relayLink.getService(), relayLink.getInstanceId(), relayLink.getKey());
	}

	static String idOf(String service, long id, String key) {
		return service + "-" + id + "-" + key;
	}

	/**
	 * @param tunnel 关闭管道
	 */
	void closeTunnel(RelayTunnel<?> tunnel);

	/**
	 * @param tunnel 打开管道
	 */
	void openTunnel(RelayTunnel<?> tunnel);

	/**
	 * 判断指定的 transporter 是否是当前通道的 transporter
	 *
	 * @param transporter 判断的transporter
	 * @return 如果是返回 true, 否则返回 false
	 */
	boolean isCurrentTransporter(RelayTransporter transporter);

	/**
	 * 发送转发数据包
	 *
	 * @param factory   数据包工厂
	 * @param arguments 数据参数
	 * @param promise   是否需要写出应答对象
	 * @return 如果promise为true返回写出应答对象, 如果promise为 false, 返回 null
	 */
	<P extends RelayPacket<A>, A extends RelayPacketArguments> MessageWriteAwaiter write(
			RelayPacketFactory<P, A> factory, A arguments, boolean promise);

	/**
	 * 发送转发数据包
	 *
	 * @param factory   数据包工厂
	 * @param arguments 数据参数
	 */
	default <P extends RelayPacket<A>, A extends RelayPacketArguments> void write(
			RelayPacketFactory<P, A> factory, A arguments) {
		write(factory, arguments, false);
	}

	/**
	 * 转发消息到目标服务器
	 *
	 * @param tunnel  tunnel
	 * @param message 消息
	 * @param awaiter 发送应答对象
	 * @return 返回转发应答对象
	 */
	MessageWriteAwaiter relay(RelayTunnel<?> tunnel, Message message, MessageWriteAwaiter awaiter);

	/**
	 * 转发消息到目标服务器
	 *
	 * @param tunnel    tunnel
	 * @param allocator 消息装配器
	 * @param factory   消息工厂
	 * @param context   消息上下文
	 * @return 返回转发应答对象
	 */
	MessageWriteAwaiter relay(RelayTunnel<?> tunnel, MessageAllocator allocator, MessageFactory factory, MessageContext context);

	/**
	 * 开启
	 */
	void open();

	/**
	 * 心跳
	 */
	void heartbeat();

	/**
	 * @return 最近一次心跳时间
	 */
	long getLatelyHeartbeatTime();

	/**
	 * ping
	 */
	void ping();

	/**
	 * pong
	 */
	void pong();

}
