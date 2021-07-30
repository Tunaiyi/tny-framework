package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class ConnectedPacket extends BaseRelayPacket {

    public ConnectedPacket(long tunnelId) {
        super(tunnelId);
    }

    public ConnectedPacket(Tubule<?> tubule) {
        super(tubule);
    }

    public ConnectedPacket(long tunnelId, long nanoTime) {
        super(tunnelId, nanoTime);
    }

    @Override
    public RelayPacketType getType() {
        return RelayPacketType.CONNECTED;
    }

}
