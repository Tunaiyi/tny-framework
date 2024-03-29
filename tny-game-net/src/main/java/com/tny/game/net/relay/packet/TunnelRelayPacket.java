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