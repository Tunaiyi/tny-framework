package com.tny.game.net.relay.cluster;

import java.util.List;

/**
 * 服务集群对象
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public interface ServeCluster {

	/**
	 * @return 集群 id
	 */
	String getId();

	/**
	 * @return 集群实例列表
	 */
	List<ServeInstance> getInstances();

}
