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

    // 心跳任务句柄：随视图终结必须取消（relay-cluster-view"周期任务可终结"契约）
    private final java.util.List<java.util.concurrent.ScheduledFuture<?>> heartbeatFutures = new java.util.concurrent.CopyOnWriteArrayList<>();

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
        // 集群匹配键与注册表键空间统一（serveName）：双名配置不再静默失联（relay-cluster-view 契约）
        NettyRemoteServeCluster cluster = this.clusterOf(node.getServeName());
        if (cluster != null) {
            addInstance(node, cluster);
        }
    }

    @Override
    public void removeInstance(ServeNode node) {
        NettyRemoteServeCluster cluster = this.clusterOf(node.getServeName());
        if (cluster != null) {
            cluster.unregisterInstance(node.getId());
        }
    }

    /**
     * 变健康
     */
    @Override
    public void updateInstance(ServeNode node, List<ServeNodeChangeStatus> statuses) {
        NettyRemoteServeCluster cluster = this.clusterOf(node.getServeName());
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
        // 先注册判重后启动：同标识并发注册的落选方不再留下已启动的孤儿连接器（relay-cluster-view 契约）
        RelayServeInstance registered = cluster.registerInstance(instance);
        if (registered != instance) {
            LOGGER.warn("instance {} 已在集群 {} 注册，忽略重复添加（不启动第二套连接器）", node.getId(), cluster.getServeName());
            return;
        }
        connectMonitor.start(instance, setting.getConnectionSize());
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
                heartbeatFutures.add(executorService.scheduleWithFixedDelay(cluster::heartbeat, heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS));
            }
        }
    }

    @Override
    public void onClosed() {
        heartbeatFutures.forEach(future -> future.cancel(false));
        heartbeatFutures.clear();
        for (NetRemoteServeCluster cluster : clusters()) {
            cluster.close();
        }
    }

}
