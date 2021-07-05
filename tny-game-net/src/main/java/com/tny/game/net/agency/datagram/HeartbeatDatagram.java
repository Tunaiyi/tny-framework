package com.tny.game.net.agency.datagram;

import com.tny.game.net.agency.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class HeartbeatDatagram extends BaseTubuleDatagram {

    public static final byte PING = 1;
    public static final byte PONG = 0;

    private byte option;

    public static HeartbeatDatagram ping(long tunnelId) {
        return new HeartbeatDatagram(tunnelId, PING);
    }

    public static HeartbeatDatagram pong(long tunnelId) {
        return new HeartbeatDatagram(tunnelId, PONG);
    }

    public HeartbeatDatagram(long id, byte option) {
        super(id);
        this.option = option;
    }

    public HeartbeatDatagram(long id, long nanoTime, byte option) {
        super(id, nanoTime);
        this.option = option;
    }

    public HeartbeatDatagram(Tubule<?> tubule, long nanoTime, byte option) {
        super(tubule, nanoTime);
        this.option = option;
    }

    public HeartbeatDatagram(Tubule<?> tubule, byte option) {
        super(tubule);
        this.option = option;
    }

    public HeartbeatDatagram(long id) {
        super(id);
    }

    public HeartbeatDatagram(long id, long nanoTime) {
        super(id, nanoTime);
    }

    public HeartbeatDatagram(Tubule<?> tubule, long nanoTime) {
        super(tubule, nanoTime);
    }

    public HeartbeatDatagram(Tubule<?> tubule) {
        super(tubule);
    }

    @Override
    public AgentDatagramHandlerType getType() {
        return AgentDatagramHandlerType.HEART_BEAT;
    }

}
