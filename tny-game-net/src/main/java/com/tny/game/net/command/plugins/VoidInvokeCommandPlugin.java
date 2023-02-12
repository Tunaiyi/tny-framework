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
package com.tny.game.net.command.plugins;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

public interface VoidInvokeCommandPlugin<UID> extends CommandPlugin<UID, Void> {

    @Override
    default Class<Void> getAttributesClass() {
        return Void.class;
    }

    /**
     * 请求过滤
     *
     * @param communicator 通道
     * @param message      消息
     * @param context      上下文
     * @throws Exception 异常
     */
    @Override
    default void execute(Tunnel<UID> communicator, Message message, RpcInvokeContext context, Void attribute) throws Exception {
        this.doExecute(communicator, message, context);
    }

    /**
     * 请求过滤
     *
     * @param communicator 通道
     * @param message      消息
     * @param context      上下文
     * @throws Exception 异常
     */
    void doExecute(Tunnel<UID> communicator, Message message, RpcInvokeContext context) throws Exception;

}