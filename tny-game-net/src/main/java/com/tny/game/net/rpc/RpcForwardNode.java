package com.tny.game.net.rpc;

import java.util.List;

/**
 * Rpc转发节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:50
 **/
public interface RpcForwardNode {

    /**
     * 通过接入 Id 获取接入点
     *
     * @param id 接入id
     * @return 返回接入点
     */
    RpcForwardAccess getForwardAccess(long id);

    /**
     * @return 获取有序的接入点列表
     */
    List<? extends RpcForwardAccess> getOrderForwardAccess();

    /**
     * @return 是否活跃
     */
    boolean isActive();

}
