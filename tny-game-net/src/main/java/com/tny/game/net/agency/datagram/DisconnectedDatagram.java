package com.tny.game.net.agency.datagram;

import com.tny.game.net.agency.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class DisconnectedDatagram extends BaseTubuleDatagram {

    public DisconnectedDatagram(long tunnelId) {
        super(tunnelId);
    }

    public DisconnectedDatagram(long tunnelId, long nanoTime) {
        super(tunnelId, nanoTime);
    }

    public DisconnectedDatagram(Tubule<?> tubule, long nanoTime) {
        super(tubule, nanoTime);
    }

    public DisconnectedDatagram(Tubule<?> tubule) {
        super(tubule);
    }

    @Override
    public AgentDatagramHandlerType getType() {
        return AgentDatagramHandlerType.DISCONNECTED;
    }

}
