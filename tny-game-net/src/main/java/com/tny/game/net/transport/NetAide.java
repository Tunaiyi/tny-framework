package com.tny.game.net.transport;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public class NetAide {

    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong(0);

    private static final AtomicLong TRANSPORT_ID_CREATOR = new AtomicLong(0);

    private static final AtomicLong SESSION_ID_CREATOR = new AtomicLong(0);

    public static long newEndpointId() {
        return SESSION_ID_CREATOR.incrementAndGet();
    }

    public static long newTunnelId() {
        return TUNNEL_ID_CREATOR.incrementAndGet();
    }

    public static long newTransportId() {
        return TRANSPORT_ID_CREATOR.incrementAndGet();
    }

}
