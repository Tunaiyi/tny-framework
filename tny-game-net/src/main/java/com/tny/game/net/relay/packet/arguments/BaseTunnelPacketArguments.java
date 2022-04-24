package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 3:30 下午
 */
public abstract class BaseTunnelPacketArguments implements TunnelPacketArguments {

    private final long tunnelId;

    private final long instanceId;

    protected BaseTunnelPacketArguments(long instanceId, long tunnelId) {
        this.instanceId = instanceId;
        this.tunnelId = tunnelId;
    }

    @Override
    public long getTunnelId() {
        return tunnelId;
    }

    @Override
    public long getInstanceId() {
        return instanceId;
    }

}
