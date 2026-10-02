/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.netty4.relay;

import com.tny.game.net.application.*;
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
    public String getService() {
        return RpcServiceTypes.checkService(appContext.serviceName()).getService();
    }


    @Override
    public long getInstanceId() {
        return appContext.getServerId();
    }


    @Override
    public String createLinkKey(String service) {
        String launchId = service + "." + this.getInstanceId() + "." + launchNum + "." + ThreadLocalRandom.current().nextInt(100000000, 1000000000);
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
