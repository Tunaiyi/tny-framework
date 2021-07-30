package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class ConnectPacket extends BaseRelayPacket {

    private final String ip;

    private final int port;

    public ConnectPacket(long id, String ip, int port) {
        super(id);
        this.ip = ip;
        this.port = port;
    }

    public ConnectPacket(Tubule<?> tubule, String ip, int port) {
        super(tubule);
        this.ip = ip;
        this.port = port;
    }

    public ConnectPacket(long id, long nanoTime, String ip, int port) {
        super(id, nanoTime);
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public RelayPacketType getType() {
        return RelayPacketType.CONNECT;
    }

}
