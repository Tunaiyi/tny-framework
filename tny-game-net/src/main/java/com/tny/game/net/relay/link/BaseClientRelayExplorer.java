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
package com.tny.game.net.relay.link;

import com.google.common.collect.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.clusters.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.exception.*;
import com.tny.game.net.relay.link.route.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.relay.cluster.ServeClusterFilterStatus.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 9:00 下午
 */
public abstract class BaseClientRelayExplorer<T extends NetRemoteServeCluster> extends BaseRelayExplorer<ClientRelayTunnel>
        implements NetClientRelayExplorer {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseClientRelayExplorer.class);

    private volatile List<T> clusters = ImmutableList.of();

    private volatile Map<String, T> clusterMap = ImmutableMap.of();

    private final ClientRelayContext context;

    public BaseClientRelayExplorer(ClientRelayContext context) {
        this.context = context;
    }

    protected void initClusters(Collection<T> clusters) {
        this.clusters = ImmutableList.copyOf(clusters);
        this.clusterMap = ImmutableMap.copyOf(clusters.stream().collect(Collectors.toMap(NetRemoteServeCluster::getServeName, ObjectAide::self)));
    }

    @Override
    public RemoteServeCluster getCluster(String id) {
        return clusterMap.get(id);
    }

    @Override
    public List<RemoteServeCluster> getClusters() {
        return as(clusters);
    }

    @Override
    public DoneResult<ClientRelayTunnel> createTunnel(long id, MessageTransport transport, NetworkContext context) {
        RelayMessageRouter relayMessageRouter = this.context.getRelayMessageRouter();
        GeneralClientRelayTunnel tunnel = new GeneralClientRelayTunnel(this.context.getInstanceId(), id, transport, context,
                relayMessageRouter);
        Map<String, ClientTunnelRelayer> relayer = preassignRelayer(tunnel);
        tunnel.initRelayers(relayer);
        return DoneResults.success(putTunnel(tunnel));
    }

    @Override
    public ClientRelayLink allotLink(ClientRelayTunnel tunnel, String service) {
        RemoteServeCluster cluster = this.getCluster(service);
        if (cluster == null) {
            return null;
        }
        return cluster.allotLink(tunnel);
    }

    // 预分配 link
    private Map<String, ClientTunnelRelayer> preassignRelayer(ClientRelayTunnel tunnel) {
        if (this.clusters.size() == 1) {
            return preassignForSingleCluster(tunnel);
        } else {
            return preassignForMultipleCluster(tunnel);
        }
    }

    // 预分配 link, 单集群
    private Map<String, ClientTunnelRelayer> preassignForSingleCluster(ClientRelayTunnel tunnel) {
        for (NetRemoteServeCluster cluster : clusters) {
            ClientTunnelRelayer relayer = assignRelayer(tunnel, cluster);
            if (relayer != null) {
                return ImmutableMap.of(relayer.getService(), relayer);
            }
        }
        return ImmutableMap.of();
    }

    // 预分配 link, 多集群
    private Map<String, ClientTunnelRelayer> preassignForMultipleCluster(ClientRelayTunnel tunnel) {
        Map<String, ClientTunnelRelayer> relayerMap = new HashMap<>();
        for (NetRemoteServeCluster cluster : clusters) {
            ClientTunnelRelayer relayer = assignRelayer(tunnel, cluster);
            if (relayer != null) {
                relayerMap.put(relayer.getService(), relayer);
            }
        }
        return relayerMap;
    }

    private ClientTunnelRelayer assignRelayer(ClientRelayTunnel tunnel, NetRemoteServeCluster cluster) {
        ServeClusterFilter serveClusterFilter = context.getServeClusterFilter();
        ServeClusterFilterStatus filterStatus = serveClusterFilter.filter(tunnel, cluster); // 过滤器
        if (filterStatus == UNNECESSARY) {
            return null;
        }
        ClientTunnelRelayer relayer = new ClientTunnelRelayer(cluster.getService(), filterStatus, this);
        ClientRelayLink link = relayer.allot(tunnel); // 分配
        if ((link == null || !link.isActive())) {
            if (filterStatus == ServeClusterFilterStatus.REQUIRED) {
                throw new RelayLinkNoFoundException("Tunnel[{}] 申请分配 {} cluster 集群无可用连接", tunnel.getRemoteAddress(),
                        cluster.getServeName());
            } else {
                LOGGER.warn("Tunnel[{}] 申请分配 {} cluster 集群无可用连接", tunnel.getRemoteAddress(), cluster.getServeName());
            }
        }
        return relayer;
    }

    protected T clusterOf(String service) {
        return clusterMap.get(service);
    }

    protected List<T> clusters() {
        return clusters;
    }

}
