/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.link;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.collection.map.access.*;
import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.relay.cluster.*;
import org.apache.commons.lang3.builder.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 4:39 上午
 */
public class BaseRemoteServeInstance implements NetRemoteServeInstance {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseRemoteServeInstance.class);

    protected final NetRemoteServeCluster cluster;

    private final long id;

    private final String appType;

    private final String scopeType;

    private final String scheme;

    private final String host;

    private final int port;

    private boolean healthy;

    private ObjectMap metadata = new ObjectMap();

    private final AtomicBoolean close = new AtomicBoolean(false);

    private Map<String, RemoteRelayLink> relayLinkMap = new ConcurrentHashMap<>();

    private volatile List<RemoteRelayLink> activeRelayLinks = ImmutableList.of();

    public BaseRemoteServeInstance(NetRemoteServeCluster cluster, ServeNode node) {
        this.id = node.getId();
        this.scheme = node.getScheme();
        this.host = node.getHost();
        this.port = node.getPort();
        this.appType = node.getAppType();
        this.scopeType = node.getScopeType();
        this.healthy = node.isHealthy();
        this.cluster = cluster;
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
    public List<RemoteRelayLink> getActiveRelayLinks() {
        return activeRelayLinks;
    }

    @Override
    public boolean isClose() {
        return close.get();
    }

    @Override
    public synchronized void close() {
        if (close.compareAndSet(false, true)) {
            this.prepareClose();
            Map<String, RemoteRelayLink> oldMap = this.relayLinkMap;
            this.relayLinkMap = new ConcurrentHashMap<>();
            oldMap.forEach((id, link) -> link.close());
            oldMap.clear();
            this.postClose();
        }
    }

    @Override
    public void heartbeat() {
        long lifeTime = cluster.getContext().getLinkMaxIdleTime();
        long now = System.currentTimeMillis();
        for (RemoteRelayLink link : relayLinkMap.values()) {
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

    private synchronized void refreshActiveLinks() {
        this.activeRelayLinks = ImmutableList.sortedCopyOf(Comparator.comparing(RemoteRelayLink::getId), relayLinkMap.values()
                .stream()
                .filter(RelayLink::isActive)
                .collect(Collectors.toList()));
        cluster.refreshInstances();
    }

    @Override
    public synchronized void register(RemoteRelayLink link) {
        NetRelayLink old = relayLinkMap.put(link.getId(), link);
        if (old != null && old != link) {
            old.close();
        }
        this.refreshActiveLinks();
        this.onRegister(link);
    }

    @Override
    public void disconnected(RemoteRelayLink link) {
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
    public synchronized void relieve(RemoteRelayLink link) {
        if (relayLinkMap.remove(link.getId(), link)) {
            if (link.isActive()) {
                link.close();
            }
            this.refreshActiveLinks();
            this.onRelieve(link);
        }
    }

    protected void onRegister(RemoteRelayLink link) {

    }

    protected void onRelieve(RemoteRelayLink link) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BaseRemoteServeInstance)) {
            return false;
        }

        BaseRemoteServeInstance that = (BaseRemoteServeInstance)o;

        return new EqualsBuilder().append(getId(), that.getId()).append(cluster, that.cluster).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(cluster).append(getId()).toHashCode();
    }

}
