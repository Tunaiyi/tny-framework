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

import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.clusters.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 2:29 下午
 */
@Unit
public class NettyClientRelayExplorer extends BaseClientRelayExplorer<NettyRemoteServeCluster> implements AppPrepareStart, AppClosed {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyClientRelayExplorer.class);

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1,
            new CoreThreadFactory("RelayReconnectScheduled"));

    private final ClientRelayContext clientRelayContext;

    public NettyClientRelayExplorer(ClientRelayContext clientRelayContext, List<NettyRemoteServeClusterContext> clusterContexts) {
        super(clientRelayContext);
        this.clientRelayContext = clientRelayContext;
        Set<NettyRemoteServeCluster> clusters = clusterContexts.stream()
                .map(NettyRemoteServeCluster::new)
                .collect(Collectors.toSet());
        this.initClusters(clusters);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(NettyClientRelayExplorer.class, LifecycleLevel.SYSTEM_LEVEL_9);
    }

    @Override
    public void putInstance(ServeNode node) {
        NettyRemoteServeCluster cluster = this.clusterOf(node.getService());
        if (cluster != null) {
            addInstance(node, cluster);
        }
    }

    @Override
    public void removeInstance(ServeNode node) {
        NettyRemoteServeCluster cluster = this.clusterOf(node.getService());
        if (cluster != null) {
            cluster.unregisterInstance(node.getId());
        }
    }

    /**
     * 变健康
     */
    @Override
    public void updateInstance(ServeNode node, List<ServeNodeChangeStatus> statuses) {
        NettyRemoteServeCluster cluster = this.clusterOf(node.getService());
        if (cluster != null) {
            if (statuses.contains(ServeNodeChangeStatus.URL_CHANGE)) {
                cluster.unregisterInstance(node.getId());
                this.putInstance(node);
                return;
            }
            if (statuses.contains(ServeNodeChangeStatus.METADATA_CHANGE)) {
                cluster.updateInstance(node);
            }
        }
    }

    private void addInstance(ServeNode node, NettyRemoteServeCluster cluster) {
        RemoteServeClusterContext context = cluster.getContext();
        NettyServeInstanceConnectMonitor connectMonitor = new NettyServeInstanceConnectMonitor(clientRelayContext, context, executorService);
        NetRelayServeInstance instance = new NettyRelayServeInstance(cluster, node, connectMonitor);
        var setting = context.getSetting();
        connectMonitor.start(instance, setting.getConnectionSize());
        cluster.registerInstance(instance);
    }

    @Override
    public void prepareStart() {
        for (NettyRemoteServeCluster cluster : this.clusters()) {
            RemoteServeClusterContext clusterContext = cluster.getContext();
            List<NetAccessNode> instances = clusterContext.getInstances();
            if (CollectionUtils.isNotEmpty(instances)) {
                instances.forEach(point -> {
                    RemoteServeNode node = new RemoteServeNode(null, null, cluster.getServeName(), cluster.getService(), point);
                    addInstance(node, cluster);
                });
            }
            var setting = clusterContext.getSetting();
            long heartbeatInterval = setting.getConnectionHeartbeatInterval();
            if (heartbeatInterval > 0) {
                executorService.scheduleWithFixedDelay(cluster::heartbeat, heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void onClosed() {
        for (NetRemoteServeCluster cluster : clusters()) {
            cluster.close();
        }
    }

}
