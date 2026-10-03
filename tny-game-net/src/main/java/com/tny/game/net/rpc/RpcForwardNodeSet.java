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

import com.tny.game.net.application.*;

import java.util.List;

/**
 * Rpc
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:49
 **/
public interface RpcForwardNodeSet {

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
    RpcAccess findForwardAccess(RpcAccessPoint servicer);

}
