package com.tny.game.net.relay.link;

/**
 * 本地转发连接
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 10:12 下午
 */
public interface RemoteRelayLink extends NetRelayLink {

	/**
	 * 连接认证
	 *
	 * @param serveName  服务名
	 * @param instanceId 实例 id
	 */
	void auth(String serveName, long instanceId);

	/**
	 * 切换link
	 *
	 * @param tunnel tunnel
	 */
	void switchTunnel(RemoteRelayTunnel<?> tunnel);

	/**
	 * 断开link 与 tunnel的关联
	 *
	 * @param tunnel tunnel
	 */
	void delinkTunnel(RelayTunnel<?> tunnel);

	//	/**
	//	 * 绑定客户端传到
	//	 *
	//	 * @param tunnel 客户端管道
	//	 * @return 成功返回true 失败返回 false
	//	 */
	//	boolean registerTunnel(RelayTunnel<?> tunnel);
	//
	//	/**
	//	 * 反注册 tunnel
	//	 *
	//	 * @param tunnel 移除的tunnel
	//	 */
	//	void unregisterTunnel(RelayTunnel<?> tunnel);

}
