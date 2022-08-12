/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelDisconnectPacket extends BaseTunnelPacket<TunnelVoidArguments> {

    public static final RelayPacketFactory<TunnelDisconnectPacket, TunnelVoidArguments> FACTORY = TunnelDisconnectPacket::new;

    public TunnelDisconnectPacket(int id, long instanceId, long tunnelId) {
        super(id, RelayPacketType.TUNNEL_DISCONNECT, new TunnelVoidArguments(instanceId, tunnelId));
    }

    public TunnelDisconnectPacket(int id, long instanceId, long tunnelId, long nanoTime) {
        super(id, RelayPacketType.TUNNEL_DISCONNECT, nanoTime, new TunnelVoidArguments(instanceId, tunnelId));
    }

    public TunnelDisconnectPacket(int id, TunnelVoidArguments arguments, long time) {
        super(id, RelayPacketType.TUNNEL_DISCONNECT, time, arguments);
    }

}
