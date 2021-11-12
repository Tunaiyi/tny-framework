package com.tny.game.net.relay.link;

import java.util.Map;

/**
 * 本地集群服务实例
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:24 下午
 */
public interface NetRemoteServeInstance extends RemoteServeInstance {

	/**
	 * 注册转发连接
	 *
	 * @param link 集群实例
	 */
	void register(RemoteRelayLink link);

	/**
	 * @param link 断开连接
	 */
	void disconnected(RemoteRelayLink link);

	/**
	 * 释放转发连接
	 *
	 * @param link 集群实例
	 */
	void relieve(RemoteRelayLink link);

	/**
	 * @return 获取登录用户名
	 */
	String getUsername();

	/**
	 * @param defaultName 默认吗
	 * @return 如果登录用户名为空, 返回 defaultName, 否则返回用户名
	 */
	String username(String defaultName);

	/**
	 * 关闭服务实例
	 */
	void close();

	/**
	 * 服务实例心跳
	 */
	void heartbeat();

	/**
	 * 变健康
	 */
	boolean updateHealthy(boolean healthy);

	/**
	 * 更新Metadata
	 *
	 * @param metadata Metadata
	 */
	void updateMetadata(Map<String, Object> metadata);

}
