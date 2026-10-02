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
