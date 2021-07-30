package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class DisconnectPacket extends BaseRelayPacket {

    public DisconnectPacket(long tunnelId) {
        super(tunnelId);
    }

    public DisconnectPacket(long tunnelId, long nanoTime) {
        super(tunnelId, nanoTime);
    }

    public DisconnectPacket(Tubule<?> tubule, long nanoTime) {
        super(tubule, nanoTime);
    }

    public DisconnectPacket(Tubule<?> tubule) {
        super(tubule);
    }

    @Override
    public RelayPacketType getType() {
        return RelayPacketType.DISCONNECT;
    }

}
