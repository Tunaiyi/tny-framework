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

    public TunnelConnectedPacket(int id, RelayTunnel<?> tunnel, boolean result) {
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
