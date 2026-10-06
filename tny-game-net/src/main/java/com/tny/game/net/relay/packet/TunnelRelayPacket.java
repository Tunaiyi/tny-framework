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

import com.tny.game.net.message.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * 传输事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelRelayPacket extends BaseTunnelPacket<TunnelRelayArguments> {

    public static final RelayPacketFactory<TunnelRelayPacket, TunnelRelayArguments> FACTORY = TunnelRelayPacket::new;

    public TunnelRelayPacket(int id, long instanceId, long tunnelId, Message message) {
        super(id, RelayPacketType.TUNNEL_RELAY, new TunnelRelayArguments(instanceId, tunnelId, message));
    }

    public TunnelRelayPacket(int id, long instanceId, long tunnelId, Message message, long time) {
        super(id, RelayPacketType.TUNNEL_RELAY, time, new TunnelRelayArguments(instanceId, tunnelId, message));
    }

    public TunnelRelayPacket(int id, TunnelRelayArguments arguments, long nanoTime) {
        super(id, RelayPacketType.TUNNEL_RELAY, nanoTime, arguments);
    }

    @Override
    protected String toTunnelPacketMessage() {
        return "中转消息";
    }

}