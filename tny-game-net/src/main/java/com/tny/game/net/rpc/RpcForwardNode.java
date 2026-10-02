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
 * Rpc转发节点
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 19:50
 **/
public interface RpcForwardNode extends RpcNode {

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
