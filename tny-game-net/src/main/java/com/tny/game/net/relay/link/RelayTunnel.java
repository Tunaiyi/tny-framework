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
package com.tny.game.net.relay.link;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 2:37 下午
 */
public interface RelayTunnel<UID> extends Tunnel<UID> {

    /**
     * @return 服务实例 id
     */
    long getInstanceId();

    /**
     * 把 message 转发到 tunnel 绑定的目标
     *
     * @param rpcContext rpc上下文
     * @param promise    发送应答对象
     * @return 返回等待对象
     */
    MessageWriteFuture relay(RpcProviderContext rpcContext, boolean promise);

}
