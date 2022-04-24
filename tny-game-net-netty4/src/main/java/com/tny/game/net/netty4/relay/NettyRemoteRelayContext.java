package com.tny.game.net.netty4.relay;

import com.tny.game.net.base.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.route.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:04 下午
 */
public class NettyRemoteRelayContext implements RemoteRelayContext {

    private NetAppContext appContext;

    private final String launchNum;

    private RelayMessageRouter relayMessageRouter;

    private ServeClusterFilter serveClusterFilter;

    private final AtomicLong indexCounter = new AtomicLong();

    public NettyRemoteRelayContext(NetAppContext appContext, RelayMessageRouter relayMessageRouter, ServeClusterFilter serveClusterFilter) {
        this.setAppContext(appContext);
        this.relayMessageRouter = relayMessageRouter;
        this.serveClusterFilter = serveClusterFilter;
        long launchAt = System.nanoTime();
        String value = String.valueOf(launchAt);
        this.launchNum = value.substring(value.length() - 12);
    }

    @Override
    public String getAppServeName() {
        return appContext.getAppType();
    }

    @Override
    public long getAppInstanceId() {
        return appContext.getServerId();
    }

    @Override
    public String createLinkKey(String service) {
        String launchId = service + "." + this.getAppInstanceId() + "." + launchNum + "." +
                ThreadLocalRandom.current().nextInt(100000000, 1000000000);
        UUID uuid = UUID.nameUUIDFromBytes((launchId + "#" + indexCounter.incrementAndGet()).getBytes(StandardCharsets.UTF_8));
        String head = Long.toUnsignedString(uuid.getMostSignificantBits(), 32);
        String tail = Long.toUnsignedString(uuid.getLeastSignificantBits(), 32);
        return head + "-" + tail;
    }

    @Override
    public RelayMessageRouter getRelayMessageRouter() {
        return relayMessageRouter;
    }

    @Override
    public ServeClusterFilter getServeClusterFilter() {
        return serveClusterFilter;
    }

    @Override
    public NetAppContext getAppContext() {
        return appContext;
    }

    public NettyRemoteRelayContext setAppContext(NetAppContext appContext) {
        this.appContext = appContext;
        return this;
    }

    public NettyRemoteRelayContext setRelayMessageRouter(RelayMessageRouter relayMessageRouter) {
        this.relayMessageRouter = relayMessageRouter;
        return this;
    }

    public NettyRemoteRelayContext setServeClusterFilter(ServeClusterFilter serveClusterFilter) {
        this.serveClusterFilter = serveClusterFilter;
        return this;
    }

}
