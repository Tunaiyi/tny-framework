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
