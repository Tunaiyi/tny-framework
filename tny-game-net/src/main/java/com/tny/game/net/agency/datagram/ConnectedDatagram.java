package com.tny.game.net.agency.datagram;

import com.tny.game.net.agency.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class ConnectedDatagram extends BaseTubuleDatagram {

    public ConnectedDatagram(long tunnelId) {
        super(tunnelId);
    }

    public ConnectedDatagram(Tubule<?> tubule) {
        super(tubule);
    }

    public ConnectedDatagram(long tunnelId, long nanoTime) {
        super(tunnelId, nanoTime);
    }

    @Override
    public AgentDatagramHandlerType getType() {
        return AgentDatagramHandlerType.CONNECTED;
    }

}
