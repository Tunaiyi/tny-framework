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

import com.tny.game.net.clusters.*;
import com.tny.game.net.relay.link.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:09 下午
 */
class NettyServeInstanceConnectMonitor {

    private final List<NettyRelayLinkConnector> connections = new CopyOnWriteArrayList<>();

    private final ClientRelayContext relayContext;

    private final RemoteServeClusterContext serveClusterContext;

    private final ScheduledExecutorService executorService;

    NettyServeInstanceConnectMonitor(
            ClientRelayContext relayContext, RemoteServeClusterContext serveClusterContext, ScheduledExecutorService executorService) {
        this.relayContext = relayContext;
        this.serveClusterContext = serveClusterContext;
        this.executorService = executorService;
    }

    protected void connect(NettyRelayLinkConnector monitor) {
        serveClusterContext.connect(monitor.getUrl(), monitor);
    }

    protected void connect(NettyRelayLinkConnector monitor, long delayTime) {
        executorService.schedule(monitor::connect, delayTime, TimeUnit.MILLISECONDS);
    }

    public synchronized void start(NetRelayServeInstance instance, int connectionSize) {
        if (!this.connections.isEmpty()) {
            return;
        }
        List<NettyRelayLinkConnector> monitors = new ArrayList<>();
        for (int i = 0; i < connectionSize; i++) {
            monitors.add(new NettyRelayLinkConnector(relayContext, instance, this));
        }
        this.connections.addAll(monitors);
        for (NettyRelayLinkConnector connector : monitors) {
            connector.connect();
        }
    }

    public synchronized void stop() {
        for (NettyRelayLinkConnector monitor : connections) {
            monitor.close();
        }
        connections.clear();
    }

}
