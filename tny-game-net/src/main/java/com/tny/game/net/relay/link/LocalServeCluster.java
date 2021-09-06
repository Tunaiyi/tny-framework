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
	 * @return 获取上下文
	 */
	LocalServeClusterContext getContext();

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
	LocalRelayLink allotLink(Tunnel<?> tunnel);

	/**
	 * 注册 LocaleServeInstance, 如果存在返回旧的 instance
	 *
	 * @param instance 注册的 instance
	 * @return 返回 instance
	 */
	LocalServeInstance registerInstance(LocalServeInstance instance);

	/**
	 * 卸载指定 instanceId 的 Instance
	 *
	 * @param instanceId 指定的Instance id
	 */
	void unregisterInstance(long instanceId);

	//	/**
	//	 * 注册 link
	//	 *
	//	 * @param link 注册的 link
	//	 * @return 返回 instance
	//	 */
	//	LocalServeInstance registerLink(NetRelayLink link);
	//
	//	/**
	//	 * 释放 link
	//	 *
	//	 * @param link 释放的link
	//	 */
	//	void relieveLink(NetRelayLink link);

}
