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
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 3:15 上午
 */
public class FirstRpcRouter implements RpcRouter {

    @Override
    public RpcAccess route(RpcInvokeNodeSet serviceSet, RpcRemoteMethod invoker, RpcRemoteInvokeParams invokeParams) {
        List<? extends RpcInvokeNode> nodes = serviceSet.getOrderInvokeNodes();
        if (nodes.isEmpty()) {
            return null;
        }
        RpcInvokeNode node = nodes.get(0);
        List<? extends RpcAccess> accessPoints = node.getOrderAccesses();
        if (accessPoints.isEmpty()) {
            return null;
        }
        return accessPoints.get(0);
    }

}
