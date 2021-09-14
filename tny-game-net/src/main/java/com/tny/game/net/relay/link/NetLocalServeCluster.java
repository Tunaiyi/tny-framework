package com.tny.game.net.relay.link;

import com.tny.game.net.relay.cluster.*;

/**
 * 本地集群客户端管理
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public interface NetLocalServeCluster extends LocalServeCluster {

	/**
	 * 关闭本地集群连接
	 */
	void close();

	/**
	 * 注册 LocaleServeInstance, 如果存在返回旧的 instance
	 *
	 * @param instance 注册的 instance
	 * @return 返回 instance
	 */
	LocalServeInstance registerInstance(NetLocalServeInstance instance);

	/**
	 * 刷新实例
	 */
	void refreshInstances();

	/**
	 * 卸载指定 instanceId 的 Instance
	 *
	 * @param instanceId 指定的Instance id
	 */
	void unregisterInstance(long instanceId);

	/**
	 * @param node 更新节点信息
	 */
	void updateInstance(ServeNode node);

}
