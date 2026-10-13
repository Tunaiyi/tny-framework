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
package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelConnectPacket extends BaseTunnelPacket<TunnelConnectArguments> {

    public static final RelayPacketFactory<TunnelConnectPacket, TunnelConnectArguments> FACTORY = TunnelConnectPacket::new;

    public TunnelConnectPacket(int id, TunnelConnectArguments arguments) {
        super(id, RelayPacketType.TUNNEL_CONNECT, arguments);
    }

    public TunnelConnectPacket(int id, TunnelConnectArguments arguments, long time) {
        super(id, RelayPacketType.TUNNEL_CONNECT, time, arguments);
    }

    @Override
    protected String toTunnelPacketMessage() {
        return "建立连接";
    }

}
