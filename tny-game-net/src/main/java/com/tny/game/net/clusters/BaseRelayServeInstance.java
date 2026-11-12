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
package com.tny.game.net.clusters;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.collection.map.access.*;
import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.apache.commons.lang3.builder.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 4:39 上午
 */
public class BaseRelayServeInstance implements NetRelayServeInstance {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseRelayServeInstance.class);

    protected final NetRemoteServeCluster cluster;

    private final long id;

    private final String appType;

    private final String scopeType;

    private final String scheme;

    private final String host;

    private final int port;

    private volatile boolean healthy;

    private final RpcServiceType serviceType;

    private ObjectMap metadata = new ObjectMap();

    private final AtomicBoolean close = new AtomicBoolean(false);

    private final Map<String, ClientRelayLink> relayLinkMap = new ConcurrentHashMap<>();

    private volatile List<ClientRelayLink> activeRelayLinks = ImmutableList.of();

    private final Lock linkLock = new ReentrantLock();

    public BaseRelayServeInstance(NetRemoteServeCluster cluster, ServeNode node) {
        this.id = node.getId();
        this.scheme = node.getScheme();
        this.host = node.getHost();
        this.port = node.getPort();
        this.appType = node.getAppType();
        this.scopeType = node.getScopeType();
        this.healthy = node.isHealthy();
        this.cluster = cluster;
        this.serviceType = RpcServiceTypes.checkService(cluster.getService());
    }

    @Override
    public String getServeName() {
        return cluster.getServeName();
    }

    @Override
    public String getService() {
        return cluster.getService();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getAppType() {
        return appType;
    }

    @Override
    public String getScopeType() {
        return scopeType;
    }

    @Override
    public RpcServiceType serviceType() {
        return serviceType;
    }

    @Override
    public String getUsername() {
        return cluster.getUsername();
    }

    @Override
    public String username(String defaultName) {
        return ifBlank(cluster.getUsername(), defaultName);
    }

    @Override
    public boolean isHealthy() {
        return !activeRelayLinks.isEmpty() && this.healthy;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public MapAccessor metadata() {
        return metadata;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public List<ClientRelayLink> getActiveRelayLinks() {
        return activeRelayLinks;
    }

    @Override
    public boolean isClose() {
        return close.get();
    }

    @Override
    public void close() {
        linkLock.lock();
        try {
            if (close.compareAndSet(false, true)) {
                this.prepareClose();
                Map<String, ClientRelayLink> snapshot = new HashMap<>(this.relayLinkMap);
                this.relayLinkMap.clear();
                snapshot.forEach((id, link) -> link.close());
                this.postClose();
            }
        } finally {
            linkLock.unlock();
        }
    }

    @Override
    public void heartbeat() {
        var context = cluster.getContext();
        var setting = context.getSetting();
        long lifeTime = setting.getConnectionMaxIdleTime();
        long now = System.currentTimeMillis();
        for (ClientRelayLink link : relayLinkMap.values()) {
            ExeAide.runQuietly(() -> {
                if (link.isActive()) {
                    link.ping();
                    if (now - link.getLatelyHeartbeatTime() > lifeTime) {
                        link.disconnect();
                    }
                }
            }, LOGGER);
        }
    }

    @Override
    public boolean updateHealthy(boolean healthy) {
        if (this.healthy != healthy) {
            this.healthy = healthy;
            return true;
        }
        return false;
    }

    @Override
    public void updateMetadata(Map<String, Object> metadata) {
        this.metadata = new ObjectMap(metadata);
    }

    protected void prepareClose() {
    }

    protected void postClose() {
    }

    private void refreshActiveLinks() {
        linkLock.lock();
        try {
            doRefreshActiveLinks();
        } finally {
            linkLock.unlock();
        }
        // 集群级刷新移出 linkLock：linkLock→instanceLock 与摘除路径反向成环（D1 锁序统一）
        cluster.refreshInstances();
    }

    /**
     * 锁内仅做快照重建；cluster.refreshInstances() 由各入口在解锁后调用（锁序契约）。
     */
    private void doRefreshActiveLinks() {
        this.activeRelayLinks = ImmutableList.sortedCopyOf(Comparator.comparing(ClientRelayLink::getId), relayLinkMap.values()
                .stream()
                .filter(RelayLink::isActive)
                .collect(Collectors.toList()));
    }

    @Override
    public void register(ClientRelayLink link) {
        ClientRelayLink evicted = null;
        linkLock.lock();
        try {
            NetRelayLink old = relayLinkMap.put(link.getId(), link);
            if (old != null && old != link) {
                evicted = (ClientRelayLink) old;
            }
            this.doRefreshActiveLinks();
            this.onRegister(link);
        } finally {
            linkLock.unlock();
        }
        // 旧链路终结与集群刷新在 linkLock 之外（锁序契约：任何路径不同时持 linkLock 求 instanceLock）
        if (evicted != null) {
            evicted.close();
        }
        cluster.refreshInstances();
    }

    @Override
    public void disconnected(ClientRelayLink link) {
        NetRelayLink current = relayLinkMap.get(link.getId());
        if (current != link) {
            return;
        }
        if (current.isActive() && !this.activeRelayLinks.contains(current) ||
            !current.isActive() && this.activeRelayLinks.contains(current)) {
            this.refreshActiveLinks();
        }
    }

    @Override
    public void relieve(ClientRelayLink link) {
        boolean relieved;
        linkLock.lock();
        try {
            relieved = relayLinkMap.remove(link.getId(), link);
            if (relieved) {
                this.doRefreshActiveLinks();
                this.onRelieve(link);
            }
        } finally {
            linkLock.unlock();
        }
        if (relieved) {
            if (link.isActive()) {
                link.close();
            }
            cluster.refreshInstances();
        }
    }

    protected void onRegister(ClientRelayLink link) {

    }

    protected void onRelieve(ClientRelayLink link) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BaseRelayServeInstance that)) {
            return false;
        }

        return new EqualsBuilder().append(getId(), that.getId()).append(cluster, that.cluster).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(cluster).append(getId()).toHashCode();
    }

}
