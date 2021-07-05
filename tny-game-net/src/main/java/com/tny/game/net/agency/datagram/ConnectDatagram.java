package com.tny.game.net.agency.datagram;

import com.tny.game.net.agency.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class ConnectDatagram extends BaseTubuleDatagram {

    private final String ip;

    private final int port;

    public ConnectDatagram(long id, String ip, int port) {
        super(id);
        this.ip = ip;
        this.port = port;
    }

    public ConnectDatagram(Tubule<?> tubule, String ip, int port) {
        super(tubule);
        this.ip = ip;
        this.port = port;
    }

    public ConnectDatagram(long id, long nanoTime, String ip, int port) {
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
    public AgentDatagramHandlerType getType() {
        return AgentDatagramHandlerType.CONNECT;
    }

}
