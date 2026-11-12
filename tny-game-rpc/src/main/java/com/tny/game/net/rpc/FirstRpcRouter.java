/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
