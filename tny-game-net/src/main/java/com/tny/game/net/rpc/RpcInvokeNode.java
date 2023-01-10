/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.rpc;

import java.util.List;

/**
 * Rpc远程节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:16
 **/
public interface RpcInvokeNode extends RpcNode {

    /**
     * @return 获取节点上所有 rpc 接入点(连接)的有序列表
     */
    List<? extends RpcAccess> getOrderAccesses();

    /**
     * 按照 AccessId 获取指定接入点
     *
     * @param accessId AccessId
     * @return 返回接入点
     */
    RpcAccess getAccess(long accessId);

    /**
     * @return 节点是否活跃(存在有存活的接入点)
     */
    boolean isActive();

}
