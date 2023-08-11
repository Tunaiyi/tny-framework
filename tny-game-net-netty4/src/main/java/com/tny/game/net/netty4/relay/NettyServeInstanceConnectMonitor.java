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

    private final List<NettyRelayLinkConnection> connections = new CopyOnWriteArrayList<>();

    private final ClientRelayContext relayContext;

    private final RemoteServeClusterContext serveClusterContext;

    private final ScheduledExecutorService executorService;

    NettyServeInstanceConnectMonitor(
            ClientRelayContext relayContext, RemoteServeClusterContext serveClusterContext, ScheduledExecutorService executorService) {
        this.relayContext = relayContext;
        this.serveClusterContext = serveClusterContext;
        this.executorService = executorService;
    }

    protected void connect(NettyRelayLinkConnection monitor) {
        serveClusterContext.connect(monitor.getUrl(), monitor);
    }

    protected void connect(NettyRelayLinkConnection monitor, long delayTime) {
        executorService.schedule(monitor::connect, delayTime, TimeUnit.MILLISECONDS);
    }

    public synchronized void start(NetRemoteServeInstance instance, int connectionSize) {
        if (!this.connections.isEmpty()) {
            return;
        }
        List<NettyRelayLinkConnection> monitors = new ArrayList<>();
        for (int i = 0; i < connectionSize; i++) {
            monitors.add(new NettyRelayLinkConnection(relayContext, instance, this));
        }
        this.connections.addAll(monitors);
        for (NettyRelayLinkConnection connector : monitors) {
            connector.connect();
        }
    }

    public synchronized void stop() {
        for (NettyRelayLinkConnection monitor : connections) {
            monitor.close();
        }
        connections.clear();
    }

}
