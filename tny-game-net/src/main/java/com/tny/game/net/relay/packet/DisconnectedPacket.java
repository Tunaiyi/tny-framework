package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class DisconnectedPacket extends BaseRelayPacket {

    public DisconnectedPacket(long tunnelId) {
        super(tunnelId);
    }

    public DisconnectedPacket(long tunnelId, long nanoTime) {
        super(tunnelId, nanoTime);
    }

    public DisconnectedPacket(Tubule<?> tubule, long nanoTime) {
        super(tubule, nanoTime);
    }

    public DisconnectedPacket(Tubule<?> tubule) {
        super(tubule);
    }

    @Override
    public RelayPacketType getType() {
        return RelayPacketType.DISCONNECTED;
    }

}
