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

import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelConnectedPacket extends BaseTunnelPacket<TunnelConnectedArguments> {

    public static final RelayPacketFactory<TunnelConnectedPacket, TunnelConnectedArguments> FACTORY = TunnelConnectedPacket::new;

    public TunnelConnectedPacket(int id, RelayTunnel tunnel, boolean result) {
        super(id, RelayPacketType.TUNNEL_CONNECTED, TunnelConnectedArguments.ofResult(tunnel.getInstanceId(), tunnel.getId(), result));
    }

    public TunnelConnectedPacket(int id, long instanceId, long tunnelId, boolean result) {
        super(id, RelayPacketType.TUNNEL_CONNECTED, TunnelConnectedArguments.ofResult(instanceId, tunnelId, result));
    }

    public TunnelConnectedPacket(int id, TunnelConnectedArguments arguments) {
        super(id, RelayPacketType.TUNNEL_CONNECTED, arguments);
    }

    public TunnelConnectedPacket(int id, TunnelConnectedArguments arguments, long time) {
        super(id, RelayPacketType.TUNNEL_CONNECTED, time, arguments);
    }

    @Override
    protected String toTunnelPacketMessage() {
        return arguments.getResult() ? "连接成功" : "连接失败 ";
    }

}
