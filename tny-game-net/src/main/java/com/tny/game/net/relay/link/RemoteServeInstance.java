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
public interface RemoteServeInstance extends ServeInstance {

	/**
	 * @return 获取本地服务实例所有转发连接
	 */
	List<RemoteRelayLink> getActiveRelayLinks();

}
