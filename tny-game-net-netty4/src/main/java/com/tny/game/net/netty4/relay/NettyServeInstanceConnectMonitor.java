/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
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

    private final List<NettyRelayLinkConnector> connectors = new CopyOnWriteArrayList<>();

    private final RemoteRelayContext relayContext;

    private final RemoteServeClusterContext serveClusterContext;

    private final ScheduledExecutorService executorService;

    NettyServeInstanceConnectMonitor(
            RemoteRelayContext relayContext, RemoteServeClusterContext serveClusterContext, ScheduledExecutorService executorService) {
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

    public synchronized void start(NetRemoteServeInstance instance, int connectionSize) {
        if (!this.connectors.isEmpty()) {
            return;
        }
        List<NettyRelayLinkConnector> monitors = new ArrayList<>();
        for (int i = 0; i < connectionSize; i++) {
            monitors.add(new NettyRelayLinkConnector(relayContext, instance, this));
        }
        this.connectors.addAll(monitors);
        for (NettyRelayLinkConnector connector : monitors) {
            connector.connect();
        }
    }

    public synchronized void stop() {
        for (NettyRelayLinkConnector monitor : connectors) {
            monitor.close();
        }
        connectors.clear();
    }

}
