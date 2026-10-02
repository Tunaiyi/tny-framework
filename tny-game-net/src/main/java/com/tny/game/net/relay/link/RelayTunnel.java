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
package com.tny.game.net.relay.link;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 2:37 下午
 */
public interface RelayTunnel extends Tunnel {

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
    MessageWriteFuture relay(RpcTransferContext rpcContext, boolean promise);

}
