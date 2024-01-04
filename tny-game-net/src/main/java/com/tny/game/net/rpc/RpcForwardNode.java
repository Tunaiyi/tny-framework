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
