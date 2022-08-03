package com.tny.game.net.rpc;

import com.tny.game.net.base.*;

import java.util.List;

/**
 * Rpc
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:49
 **/
public interface RpcForwardSet {

    /**
     * @return 服务类型
     */
    RpcServiceType getServiceType();

    /**
     * @return 有序的转发节点
     */
    List<? extends RpcForwardNode> getOrderForwarderNodes();

    /**
     * 查指定服务者的接入点
     *
     * @param servicer 服务者
     * @return 返回接入点
     */
    RpcRemoterAccess findForwardAccess(RpcServicerPoint servicer);

}
