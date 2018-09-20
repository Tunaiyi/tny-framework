package com.tny.game.net.transport;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 2017/3/24.
 */
public class NetAide {

    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong(0);

    private static final AtomicLong SESSION_ID_CREATOR = new AtomicLong(0);

    public static long newSessionID() {
        return SESSION_ID_CREATOR.incrementAndGet();
    }

    public static long newTunnelID() {
        return TUNNEL_ID_CREATOR.incrementAndGet();
    }

}
