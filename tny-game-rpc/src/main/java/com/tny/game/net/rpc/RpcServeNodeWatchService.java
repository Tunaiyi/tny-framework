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

package com.tny.game.net.rpc;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.url.*;
import com.tny.game.net.application.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.cluster.watch.*;
import com.tny.game.net.rpc.setting.*;
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

    private final Set<RpcClientFactory> factories = new ConcurrentHashSet<>();

    private final ServeNodeClient serveNodeClient;

    public RpcServeNodeWatchService(ServeNodeClient serveNodeClient, Collection<RpcClientFactory> connectors) {
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
        for (RpcClientFactory factory : factories) {
            RpcServeNodeWatcher watcher = new RpcServeNodeWatcher(factory);
            if (watchers.add(watcher)) {
                watcher.start();
            }
        }
    }

    private static class RpcClient {

        private final int index;

        private final RpcClientFactory clientCreator;

        private Client client;

        private RpcClient(int index, RpcClientFactory clientCreator) {
            this.index = index;
            this.clientCreator = clientCreator;
        }

        void connect(URL url) {
            if (client == null) {
                client = clientCreator.create(index, url);
                ClientConnectFuture future = client.open();
                future.handle((cl, cause) -> {
                    if (cause != null) {
                        LOGGER.warn("Rpc [{}] Client {} connect failed", clientCreator.getService(), url, cause);
                    } else {
                        LOGGER.info("Rpc [{}] Client {} connect success", clientCreator.getService(), url);
                    }
                    return cl;
                });
            }
        }

        void tryReconnect() {
            if (!client.isActive()) {
                client.reconnect();
            }
        }

        public void close() {
            if (client != null) {
                client.close();
                client = null;
            }
        }

    }

    private class RpcServeNodeWatcher implements ServeNodeListener {

        private final RpcClientFactory connector;

        private volatile List<RpcClient> clients = ImmutableList.of();

        private RpcServeNodeWatcher(RpcClientFactory connector) {
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
