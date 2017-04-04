package com.tny.game.net.tunnel;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public class Tunnels {

    private static final long TUNNEL_START_ID = 1L;
    private static final long NONE_ID = TUNNEL_START_ID - 1;

    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong(0);

    public static long newID() {
        return TUNNEL_ID_CREATOR.incrementAndGet();
    }

    public static long noneID() {
        return NONE_ID;
    }

    public static boolean isTunnelID(long tunnelID) {
        return tunnelID >= TUNNEL_START_ID;
    }


    private final static ConcurrentMap<Long, Reference<Tunnel>> tunnels = new ConcurrentHashMap<>();

    public static void put(Tunnel tunnel) {
        tunnels.putIfAbsent(tunnel.getID(), new WeakReference<>(tunnel));
    }

    public static void remove(Tunnel tunnel) {
        Reference<Tunnel> reference = tunnels.get(tunnel.getID());
        if (reference == null)
            return;
        Tunnel refObject = reference.get();
        if (refObject == null || refObject == tunnel)
            tunnels.remove(tunnel.getID(), reference);
    }

    @SuppressWarnings("unchecked")
    public static <UID> Tunnel<UID> get(long id) {
        Reference<Tunnel> reference = tunnels.get(id);
        if (reference == null)
            return null;
        Tunnel refObject = reference.get();
        if (refObject == null)
            tunnels.remove(id, reference);
        return refObject;
    }


}
