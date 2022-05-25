package com.tny.game.net.rpc;

import java.util.List;

/**
 * Rpc远程节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:16
 **/
public interface RpcRemoterNode {

    /**
     * @return 服务器 id
     */
    int getNodeId();

    /**
     * @return 获取节点上所有 rpc 接入点(连接)的有序列表
     */
    List<? extends RpcRemoterAccess> getOrderRemoterAccesses();

    /**
     * 按照 AccessId 获取指定接入点
     *
     * @param accessId AccessId
     * @return 返回接入点
     */
    RpcRemoterAccess getRemoterAccess(long accessId);

    /**
     * @return 节点是否活跃(存在有存活的接入点)
     */
    boolean isActive();

}
