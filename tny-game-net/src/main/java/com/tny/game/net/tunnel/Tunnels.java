package com.tny.game.net.tunnel;

import com.tny.game.common.concurrent.CoreThreadFactory;
import com.tny.game.net.netty.NettyTunnel;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public class Tunnels {

    private static final AtomicLong TUNNEL_ID_CREATOR = new AtomicLong(0);

    private final static ConcurrentMap<Long, Reference<Tunnel>> tunnels = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("NettyTunnelPing"));

    public static long newID() {
        return TUNNEL_ID_CREATOR.incrementAndGet();
    }

    static {
        service.scheduleAtFixedRate(() -> {
            for (Entry<Long, Reference<Tunnel>> entry : tunnels.entrySet()) {
                Reference<Tunnel> tunnelRef = entry.getValue();
                Tunnel tunnel = tunnelRef.get();
                if (tunnel == null)
                    tunnels.remove(entry.getKey());
                if (tunnel instanceof NettyTunnel)
                    ((NettyTunnel) tunnel).ping();
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

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
