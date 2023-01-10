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
 * Rpc远程节点集合
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:13
 **/
public interface RpcInvokeNodeSet {

    /**
     * @return 获取有序远程节点列表
     */
    List<? extends RpcInvokeNode> getOrderInvokeNodes();

    /**
     * @return 查找远程节点
     */
    RpcInvokeNode findInvokeNode(int nodeId);

    /**
     * @return 查找远程接入(连接)
     */
    RpcAccess findInvokeAccess(int nodeId, long accessId);

}
