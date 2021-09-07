package com.tny.game.net.relay.link;

import com.tny.game.net.relay.cluster.*;

import java.util.List;

/**
 * 本地集群服务实例
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:24 下午
 */
public interface LocalServeInstance extends ServeInstance {

	/**
	 * @return 获取本地服务实例所有转发连接
	 */
	List<LocalRelayLink> getRelayLinks();

	/**
	 * 注册转发连接
	 *
	 * @param link 集群实例
	 */
	void register(LocalRelayLink link);

	/**
	 * 释放转发连接
	 *
	 * @param link 集群实例
	 */
	void relieve(LocalRelayLink link);

	/**
	 * 关闭服务实例
	 */
	void close();

	/**
	 * 服务实例心跳
	 */
	void heartbeat();

}
