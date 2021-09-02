package com.tny.game.net.relay.link;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.transport.*;

import java.util.List;

/**
 * 本地集群客户端管理
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public interface LocalServeCluster extends ServeCluster {

	/**
	 * @return 集群是否关闭
	 */
	boolean isClose();

	/**
	 * 关闭本地集群连接
	 */
	void close();

	/**
	 * @return 集群实例列表
	 */
	List<LocalServeInstance> getLocalInstances();

	/**
	 * 分配 link 给指定 tunnel
	 *
	 * @param tunnel 指定 tunnel
	 * @return 返回分配的 link
	 */
	LocalRelayLink allotLink(NetTunnel<?> tunnel);

	/**
	 * 加载 LocaleServeInstance, 如果没有则创建
	 *
	 * @param node 获取 instance 的节点
	 * @return 返回 instance
	 */
	LocalServeInstance loadInstance(ServeNode node);

	/**
	 * 卸载指定 instanceId 的 Instance
	 *
	 * @param instanceId 指定的Instance id
	 */
	void unloadInstance(long instanceId);

	/**
	 * 注册 link
	 *
	 * @param link 注册的 link
	 * @return 返回 instance
	 */
	LocalServeInstance registerLink(NetRelayLink link);

	/**
	 * 释放 link
	 *
	 * @param link 释放的link
	 */
	void relieveLink(NetRelayLink link);

}
