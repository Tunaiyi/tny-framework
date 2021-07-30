package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class HeartbeatPacket extends BaseRelayPacket {

    public static final byte PING = 1;
    public static final byte PONG = 0;

    private byte option;

    public static HeartbeatPacket ping(long tunnelId) {
        return new HeartbeatPacket(tunnelId, PING);
    }

    public static HeartbeatPacket pong(long tunnelId) {
        return new HeartbeatPacket(tunnelId, PONG);
    }

    public HeartbeatPacket(long id, byte option) {
        super(id);
        this.option = option;
    }

    public HeartbeatPacket(long id, long nanoTime, byte option) {
        super(id, nanoTime);
        this.option = option;
    }

    public HeartbeatPacket(Tubule<?> tubule, long nanoTime, byte option) {
        super(tubule, nanoTime);
        this.option = option;
    }

    public HeartbeatPacket(Tubule<?> tubule, byte option) {
        super(tubule);
        this.option = option;
    }

    public HeartbeatPacket(long id) {
        super(id);
    }

    public HeartbeatPacket(long id, long nanoTime) {
        super(id, nanoTime);
    }

    public HeartbeatPacket(Tubule<?> tubule, long nanoTime) {
        super(tubule, nanoTime);
    }

    public HeartbeatPacket(Tubule<?> tubule) {
        super(tubule);
    }

    @Override
    public RelayPacketType getType() {
        return RelayPacketType.HEART_BEAT;
    }

}
