package com.tny.game.net.agency.datagram;

import com.tny.game.net.agency.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class DisconnectDatagram extends BaseTubuleDatagram {

    public DisconnectDatagram(long tunnelId) {
        super(tunnelId);
    }

    public DisconnectDatagram(long tunnelId, long nanoTime) {
        super(tunnelId, nanoTime);
    }

    public DisconnectDatagram(Tubule<?> tubule, long nanoTime) {
        super(tubule, nanoTime);
    }

    public DisconnectDatagram(Tubule<?> tubule) {
        super(tubule);
    }

    @Override
    public AgentDatagramHandlerType getType() {
        return AgentDatagramHandlerType.DISCONNECT;
    }

}
