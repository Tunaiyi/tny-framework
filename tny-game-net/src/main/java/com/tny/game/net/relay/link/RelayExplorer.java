package com.tny.game.net.relay.link;

/**
 * 远程(客户端连接不在本地)转发服务
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:31 下午
 */
public interface RelayExplorer<T extends RelayTunnel<?>> {

	/**
	 * 获取指定的 tunnel
	 *
	 * @param instanceId 创建 tunnel 的服务实例 id
	 * @param tunnelId   管道 id
	 */
	T getTunnel(long instanceId, long tunnelId);

	/**
	 * 关闭指定的 tunnel
	 *
	 * @param instanceId 创建 tunnel 的服务实例id
	 * @param tunnelId   tunnel id
	 */
	void closeTunnel(long instanceId, long tunnelId);

}
