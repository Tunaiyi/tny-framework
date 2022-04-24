package com.tny.game.net.relay.packet.arguments;

import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 3:26 下午
 */
public class TunnelVoidArguments extends BaseTunnelPacketArguments {

    public TunnelVoidArguments(TunnelPacketArguments arguments) {
        super(arguments.getInstanceId(), arguments.getTunnelId());
    }

    public TunnelVoidArguments(RelayTunnel<?> tunnel) {
        super(tunnel.getInstanceId(), tunnel.getId());
    }

    public TunnelVoidArguments(long instanceId, long tunnelId) {
        super(instanceId, tunnelId);
    }

}
