package com.tny.game.net.relay.link;

import com.tny.game.common.event.trigger.*;
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

	//	static String idOf(String clusterId, long instanceId, InetSocketAddress address) {
	//		InetAddress inetAddress = address.getAddress();
	//		return clusterId + "#" + instanceId + "#" + inetAddress.getHostName() + ":" + address.getPort();
	//	}

	static String idOf(NetRelayLink relayLink) {
		return idOf(relayLink.getClusterId(), relayLink.getInstanceId(), relayLink.getKey());
	}

	static String idOf(String clusterId, long id, String key) {
		return clusterId + "#" + id + "#" + key;
	}

	/**
	 * 关闭指定Id的 tunnel
	 *
	 * @param tunnelId 关闭的tunnel id
	 */
	void closeTunnel(long tunnelId);

	/**
	 * 移出指定 tunnel
	 *
	 * @param tunnel 移除的tunnel
	 */
	void destroyTunnel(NetTunnel<?> tunnel);

	/**
	 * 查找 tunnelId 对应通讯管道
	 *
	 * @param tunnelId 通讯管道id
	 * @return 返回查找的通讯管道
	 */
	<UID> NetTunnel<UID> getTunnel(long tunnelId);

	/**
	 * @return 获取转发送器 id
	 */
	RelayTransporter getTransporter();

	/**
	 * 发送转发数据包
	 *
	 * @param factory   数据包工厂
	 * @param arguments 数据参数
	 * @param promise   是否需要写出应答对象
	 * @return 如果promise为true返回写出应答对象, 如果promise为 false, 返回 null
	 */
	<P extends RelayPacket<A>, A extends RelayPacketArguments> WriteMessageFuture write(RelayPacketFactory<P, A> factory, A arguments,
			boolean promise);

	/**
	 * 发送转发数据包
	 *
	 * @param factory   数据包工厂
	 * @param arguments 数据参数
	 */
	default <P extends RelayPacket<A>, A extends RelayPacketArguments> void write(RelayPacketFactory<P, A> factory, A arguments) {
		write(factory, arguments, false);
	}

	/**
	 * 开启
	 */
	void open();

	/**
	 * ping
	 */
	void ping();

	/**
	 * pong
	 */
	void pong();

}
