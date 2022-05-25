package com.tny.game.net.rpc;

import java.util.List;

/**
 * Rpc远程节点集合
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:13
 **/
public interface RpcRemoterSet {

    /**
     * @return 获取有序远程节点列表
     */
    List<? extends RpcRemoterNode> getOrderRemoterNodes();

    /**
     * @return 获取有序远程节点列表
     */
    RpcRemoterNode findRemoterNode(int nodeId);

    /**
     * @return 获取有序远程节点列表
     */
    RpcRemoterAccess findRemoterAccess(int nodeId, long accessId);

}
