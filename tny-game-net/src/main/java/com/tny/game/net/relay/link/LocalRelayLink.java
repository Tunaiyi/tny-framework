package com.tny.game.net.relay.link;

/**
 * 本地转发连接
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 10:12 下午
 */
public interface LocalRelayLink extends NetRelayLink {

	/**
	 * 连接认证
	 *
	 * @param clusterId  集群 id
	 * @param instanceId 实例 id
	 */
	void auth(String clusterId, long instanceId);

	/**
	 * 绑定客户端传到
	 *
	 * @param tunnel 客户端管道
	 * @return 成功返回true 失败返回 false
	 */
	boolean bindTunnel(NetRelayTunnel<?> tunnel);

}
