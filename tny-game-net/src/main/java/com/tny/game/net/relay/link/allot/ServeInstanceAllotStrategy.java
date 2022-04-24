package com.tny.game.net.relay.link.allot;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;

/**
 * 转发目标服务实例分配策略
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 8:11 下午
 */
public interface ServeInstanceAllotStrategy {

    /**
     * 分配服务实例
     *
     * @param tunnel  分配的通讯管道
     * @param cluster 目标集群
     * @return 返回服务实例
     */
    RemoteServeInstance allot(Tunnel<?> tunnel, NetRemoteServeCluster cluster);

}