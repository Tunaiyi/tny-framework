package com.tny.game.net.relay.link;

/**
 * 远程(客户端连接不在本地)转发服务
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:31 下午
 */
public interface RemoteRelayExplorer {

	/**
	 * 接收打开的 link
	 *
	 * @param transporter 转发器
	 * @param clusterId   集群 id
	 * @param instance    实例 id
	 */
	void acceptOpenLink(NetRelayTransporter transporter, String clusterId, long instance, String key);

	/**
	 * 接收连接的 Tunnel
	 *
	 * @param link     关联的 link
	 * @param tunnelId 管道 id
	 * @param ip       远程ip
	 * @param port     远程端口
	 */
	void acceptConnectTunnel(NetRelayLink link, long tunnelId, String ip, int port);

}
