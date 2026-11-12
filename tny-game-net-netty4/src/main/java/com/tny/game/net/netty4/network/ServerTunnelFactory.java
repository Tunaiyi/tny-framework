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
package com.tny.game.net.netty4.network;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 7:25 下午
 */
@Unit
public class ServerTunnelFactory implements NettyTunnelFactory {

    @Override
    public NetTunnel create(long id, Channel channel, NetworkContext context) {
        MessageTransport transport = new NettyChannelMessageTransport(NetAccessMode.SERVER, channel);
        return new GeneralServerTunnel(id, transport, context); // 创建 Tunnel 已经transport.bind
    }

}
