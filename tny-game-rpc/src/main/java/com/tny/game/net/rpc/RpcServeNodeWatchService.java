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

package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.url.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.cluster.watch.*;
import com.tny.game.net.rpc.setting.*;
import com.tny.game.net.session.*;
import org.slf4j.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/13 8:50 下午
 */
public class RpcServeNodeWatchService implements AppPrepareStart, AppClosed {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcServeNodeWatchService.class);

    private final Set<RpcServeNodeWatcher> watchers = new ConcurrentHashSet<>();

    private final Set<RpcConnectorFactory> factories = new ConcurrentHashSet<>();

    private final ServeNodeClient serveNodeClient;

    public RpcServeNodeWatchService(ServeNodeClient serveNodeClient, Collection<RpcConnectorFactory> connectors) {
        this.serveNodeClient = serveNodeClient;
        this.factories.addAll(connectors);
    }

    @Override
    public void onClosed() {
        for (RpcServeNodeWatcher watcher : watchers) {
            watcher.stop();
        }
    }

    @Override
    public void prepareStart() {
        for (RpcConnectorFactory factory : factories) {
            RpcServeNodeWatcher watcher = new RpcServeNodeWatcher(factory);
            if (watchers.add(watcher)) {
                watcher.start();
            }
        }
    }

    private static class RpcClient {

        private final int index;

        private final RpcConnectorFactory connectorFactory;

        private TunnelConnector connector;

        private RpcClient(int index, RpcConnectorFactory connectorFactory) {
            this.index = index;
            this.connectorFactory = connectorFactory;
        }

        void connect(URL url) {
            if (connector == null) {
                connector = connectorFactory.create(index, url);
                CompletionStageFuture<Session> future = connector.open();
                future.handle((cl, cause) -> {
                    if (cause != null) {
                        LOGGER.warn("Rpc [{}] Client {} connect failed", connectorFactory.getService(), url, cause);
                    } else {
                        LOGGER.info("Rpc [{}] Client {} connect success", connectorFactory.getService(), url);
                    }
                    return cl;
                });
            }
        }

        void tryReconnect() {
            if (connector.status() == TunnelConnectorStatus.DISCONNECT) {
                connector.reconnect();
            }
        }

        public void close() {
            if (connector != null) {
                connector.close();
                connector = null;
            }
        }

    }

    private class RpcServeNodeWatcher implements ServeNodeListener {

        private final RpcConnectorFactory connector;

        private volatile List<RpcClient> clients = ImmutableList.of();

        private RpcServeNodeWatcher(RpcConnectorFactory connector) {
            this.connector = connector;
        }

        private void connect(URL url) {
            int size = connector.getSetting().getConnectSize();
            int newSize = size - clients.size();
            if (newSize > 0) {
                List<RpcClient> clients = new ArrayList<>(this.clients);
                for (int i = 0; i < newSize; i++) {
                    RpcClient client = new RpcClient(i, connector);
                    clients.add(client);
                    client.connect(url);
                }
                this.clients = ImmutableList.copyOf(clients);
            }
            for (RpcClient client : this.clients) {
                client.tryReconnect();
            }
        }

        private void start() {
            if (connector.isDiscovery()) {
                serveNodeClient.subscribe(connector.getServeName(), this);
            } else {
                RpcServiceSetting setting = connector.getSetting();
                setting.url().ifPresent(this::connect);
            }
        }

        private void close() {
            List<RpcClient> clients = this.clients;
            this.clients = ImmutableList.of();
            for (RpcClient client : clients) {
                client.close();
            }
        }

        private void stop() {
            if (connector.isDiscovery()) {
                serveNodeClient.unsubscribe(connector.getServeName(), this);
            }
            this.close();
        }

        @Override
        public void onChange(ServeNode node, List<ServeNodeChangeStatus> statuses) {
            LOGGER.info("RPC ServeNode {} change {}", node, statuses);
            if (statuses.contains(ServeNodeChangeStatus.URL_CHANGE)) {
                this.close();
            }
            this.connect(node.url());
        }

        @Override
        public void onRemove(ServeNode node) {
            LOGGER.info("RPC ServeNode {} remove", node);
            this.close();
        }

        @Override
        public void onCreate(ServeNode node) {
            LOGGER.info("RPC ServeNode {} create", node);
            this.connect(node.url());
        }

    }

}
