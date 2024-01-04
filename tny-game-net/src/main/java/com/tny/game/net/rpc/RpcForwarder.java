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

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:45 上午
 */
@UnitInterface
public interface RpcForwarder {

    /**
     * 转发
     *
     * @param message       消息
     * @param forwardHeader 转发消息头
     * @return 返回
     */
    RpcForwardAccess forward(Message message, RpcForwardHeader forwardHeader);

    /**
     * 广播
     *
     * @param message       消息
     * @param forwardHeader 转发消息头
     * @return 返回
     */
    List<RpcForwardAccess> broadcast(Message message, RpcForwardHeader forwardHeader);

}
