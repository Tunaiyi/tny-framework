package com.tny.game.net.relay.cluster;

import com.tny.game.net.serve.*;

import java.util.List;

/**
 * 服务集群对象
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public interface ServeCluster extends Serve {

	/**
	 * @return 集群实例列表
	 */
	List<ServeInstance> getInstances();

}
