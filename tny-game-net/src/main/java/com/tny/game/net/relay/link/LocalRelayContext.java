package com.tny.game.net.relay.link;

import com.tny.game.net.base.*;
import com.tny.game.net.relay.link.route.*;

/**
 * 本地转发服务上下问
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 2:47 下午
 */
public interface LocalRelayContext {

	/**
	 * @return 获取前服务集群 id
	 */
	String getClusterId();

	/**
	 * @return 获取当前服务实例 id
	 */
	long getInstanceId();

	/**
	 * @return 分配 link id
	 */
	String createLinkId();

	/**
	 * @return 获取 message 路由器
	 */
	RelayMessageRouter getRelayMessageRouter();

	/**
	 * @return 获取 ServeCluster 过滤器
	 */
	ServeClusterFilter getServeClusterFilter();

	/**
	 * @return 获取网络应用上下问
	 */
	NetAppContext getAppContext();

}
