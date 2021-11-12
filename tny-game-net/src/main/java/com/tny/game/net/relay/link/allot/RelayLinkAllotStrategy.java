package com.tny.game.net.relay.link.allot;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;

/**
 * 转发目标连接分配策略
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 8:11 下午
 */
public interface RelayLinkAllotStrategy {

	/**
	 * 分配转发连接
	 *
	 * @param tunnel   分配的通讯管道
	 * @param instance 目标服务实例
	 * @return 返回分配的转发目标连接
	 */
	RemoteRelayLink allot(Tunnel<?> tunnel, RemoteServeInstance instance);

}