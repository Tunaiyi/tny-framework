package com.tny.game.net.relay.cluster;

/**
 * 集群实例
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:21 下午
 */
public interface ServeInstance extends ServeNode {

	/**
	 * @return 是否关闭
	 */
	boolean isClose();

}
