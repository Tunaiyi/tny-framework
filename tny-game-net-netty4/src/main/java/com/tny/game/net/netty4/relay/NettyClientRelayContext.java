/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
public class NettyClientRelayContext implements ClientRelayContext {

    private NetAppContext appContext;

    private final String launchNum;

    private RelayMessageRouter relayMessageRouter;

    private ServeClusterFilter serveClusterFilter;

    private final AtomicLong indexCounter = new AtomicLong();

    public NettyClientRelayContext(NetAppContext appContext, RelayMessageRouter relayMessageRouter, ServeClusterFilter serveClusterFilter) {
        this.setAppContext(appContext);
        this.relayMessageRouter = relayMessageRouter;
        this.serveClusterFilter = serveClusterFilter;
        long launchAt = System.nanoTime();
        String value = String.valueOf(launchAt);
        this.launchNum = value.substring(value.length() - 12);
    }

    @Override
    public String getAppServeName() {
        return RpcServiceTypes.checkAppType(appContext.appType()).getService();
    }

    @Override
    public long getAppInstanceId() {
        return appContext.getServerId();
    }

    @Override
    public AppType getAppType() {
        return appContext.appType();
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

    public NettyClientRelayContext setAppContext(NetAppContext appContext) {
        this.appContext = appContext;
        return this;
    }

    public NettyClientRelayContext setRelayMessageRouter(RelayMessageRouter relayMessageRouter) {
        this.relayMessageRouter = relayMessageRouter;
        return this;
    }

    public NettyClientRelayContext setServeClusterFilter(ServeClusterFilter serveClusterFilter) {
        this.serveClusterFilter = serveClusterFilter;
        return this;
    }

}
