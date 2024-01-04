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
package com.tny.game.net.netty4.relay.cluster;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.clusters.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.cluster.watch.*;
import com.tny.game.net.relay.link.*;
import org.slf4j.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/13 8:50 下午
 */
public class RelayRemoteServeNodeWatchService implements AppPrepareStart, AppClosed {

    public static final Logger LOGGER = LoggerFactory.getLogger(RelayRemoteServeNodeWatchService.class);

    private final Set<ServeInstanceWatcher> watchers = new ConcurrentHashSet<>();

    private final NetClientRelayExplorer localRelayExplorer;

    private final ServeNodeClient serveNodeClient;

    public RelayRemoteServeNodeWatchService(ServeNodeClient serveNodeClient, NetClientRelayExplorer localRelayExplorer) {
        this.serveNodeClient = serveNodeClient;
        this.localRelayExplorer = localRelayExplorer;
    }

    @Override
    public void onClosed() {
        for (ServeInstanceWatcher watcher : watchers) {
            watcher.stop();
        }
    }

    @Override
    public void prepareStart() {
        for (RemoteServeCluster cluster : localRelayExplorer.getClusters()) {
            RemoteServeClusterContext context = cluster.getContext();
            var setting = context.getSetting();
            if (setting.isDiscovery()) {
                ServeInstanceWatcher watcher = new ServeInstanceWatcher(cluster);
                if (watchers.add(watcher)) {
                    watcher.start();
                }
            }
        }
    }

    private class ServeInstanceWatcher implements ServeNodeListener {

        private final RemoteServeCluster cluster;

        private void start() {
            serveNodeClient.subscribe(cluster.getServeName(), this);
        }

        private void stop() {
            serveNodeClient.unsubscribe(cluster.getServeName(), this);
        }

        private ServeInstanceWatcher(RemoteServeCluster cluster) {
            this.cluster = cluster;
        }

        @Override
        public void onChange(ServeNode node, List<ServeNodeChangeStatus> statuses) {
            LOGGER.info("ServeNode {} change {}", node, statuses);
            localRelayExplorer.updateInstance(node, statuses);
        }

        @Override
        public void onRemove(ServeNode node) {
            LOGGER.info("ServeNode {} remove", node);
            localRelayExplorer.removeInstance(node);
        }

        @Override
        public void onCreate(ServeNode node) {
            LOGGER.info("ServeNode {} create", node);
            localRelayExplorer.putInstance(node);
        }

    }

}
