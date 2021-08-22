package com.tny.game.relay.transport;

import com.tny.game.net.transport.*;

import javax.annotation.Nullable;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 4:58 下午
 */
public interface RelayLinkExplorer<UID> {

	/**
	 * 获取Tunnel
	 *
	 * @param tunnelId 代理Tunnel
	 * @return 返回 获取Tunnel, 无则返回 null
	 */
	@Nullable
	NetTunnel<UID> getTunnel(long tunnelId);

	/**
	 * 关闭 repeater
	 *
	 * @param tunnelId 代理tunnel id
	 * @return 返回用户对应的 repeater, 没有则返回 null
	 */
	NetTunnel<UID> closeTunnel(long tunnelId);

	/**
	 * 移出指定 tunnel
	 *
	 * @param tunnel 移除的tunnel
	 */
	void destroyTunnel(RelayTunnel<UID> tunnel);

	/**
	 * @param tunnelId 通道id
	 * @param host     客户端远程 host
	 * @param port     客户端远程 port
	 * @return 返回穿件的 tunnel
	 */
	NetTunnel<UID> acceptTunnel(long tunnelId, String host, int port);

	/**
	 * 绑定转发的管道
	 *
	 * @param tunnel 绑定的管道
	 * @return 返回 绑定成功返回 true, 否则返回 false
	 */
	boolean bindTunnel(NetTunnel<UID> tunnel);

	void close();

}
