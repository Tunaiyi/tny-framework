package com.tny.game.net.relay.link;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

/**
 * 远程(客户端连接不在本地)转发服务
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:31 下午
 */
@UnitInterface
public interface RemoteRelayExplorer extends RelayExplorer<RemoteRelayTunnel<?>> {

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
	 * @param link           关联的 link
	 * @param networkContext 网络上下文
	 * @param instanceId     服务实例 id
	 * @param tunnelId       管道 id
	 * @param ip             远程ip
	 * @param port           远程端口
	 */
	void acceptConnectTunnel(NetRelayLink link, NetworkContext networkContext, long instanceId, long tunnelId, String ip, int port);

	/**
	 * 切换 tunnel link
	 *
	 * @param link       转发连接
	 * @param instanceId tunnel的服务实例 id
	 * @param tunnelId   tunnel id
	 */
	void switchTunnelLink(NetRelayLink link, long instanceId, long tunnelId);

}
