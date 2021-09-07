package com.tny.game.net.relay.link;

import java.util.Set;

/**
 * 本地可转发的通讯管道
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 4:47 下午
 */
public interface LocalRelayTunnel<UID> extends NetRelayTunnel<UID> {

	/**
	 * 绑定转发连接
	 *
	 * @param link 转发连接
	 */
	void bindLink(LocalRelayLink link);

	/**
	 * 解绑转发连接
	 *
	 * @param link 转发连接
	 */
	void unbindLink(LocalRelayLink link);

	/**
	 * 更具 集群id 获取转发连接
	 *
	 * @param clusterId 集群 id
	 * @return 返回获取的转发连接
	 */
	LocalRelayLink getLink(String clusterId);

	/**
	 * @return 获取所有转发连接的 key
	 */
	Set<String> getLinkKeys();

}