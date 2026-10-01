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

/**
 * {@link VoidCommandPlugin} 与 {@link VoidInvokeCommandPlugin} 的包内共享骨架。
 * <p>
 * 两个公开标记接口此前是逐字克隆（默认方法体 + {@code doExecute} 抽象声明一致），
 * 差异仅在类型名。此处收敛为单一实现：公共签名冻结，两接口各自留作互不相关的兄弟标记，
 * 从而保持既有 {@code instanceof} 语义与插件注册链零变更。
 */
interface VoidCommandPluginSupport extends CommandPlugin<Void> {

    @Override
    default Class<Void> getAttributesClass() {
        return Void.class;
    }

    /**
     * 请求过滤
     *
     * @param tunnel  通道
     * @param message 消息
     * @param context 上下文
     * @throws Exception 异常
     */
    @Override
    default void execute(Tunnel tunnel, Message message, RpcInvokeContext context, Void attribute) throws Exception {
        this.doExecute(tunnel, message, context);
    }

    /**
     * 请求过滤
     *
     * @param tunnel  通道
     * @param message 消息
     * @param context 上下文
     * @throws Exception 异常
     */
    void doExecute(Tunnel tunnel, Message message, RpcInvokeContext context) throws Exception;

}
