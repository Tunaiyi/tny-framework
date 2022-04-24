package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelSwitchLinkPacket extends BaseTunnelPacket<TunnelVoidArguments> {

    public static final RelayPacketFactory<TunnelSwitchLinkPacket, TunnelVoidArguments> FACTORY = TunnelSwitchLinkPacket::new;

    public TunnelSwitchLinkPacket(int id, TunnelVoidArguments arguments) {
        super(id, RelayPacketType.TUNNEL_SWITCH_LINK, arguments);
    }

    public TunnelSwitchLinkPacket(int id, TunnelVoidArguments arguments, long time) {
        super(id, RelayPacketType.TUNNEL_CONNECT, time, arguments);
    }

}
