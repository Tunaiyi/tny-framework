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
package com.tny.game.net.clusters;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.allot.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.builder.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 4:37 上午
 */
public abstract class BaseRemoteServeCluster implements NetRemoteServeCluster {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseRemoteServeCluster.class);

    private final String service;

    private final String serveName;

    private final String username;

    private final ServeInstanceAllotStrategy instanceAllotStrategy;

    private final RelayLinkAllotStrategy relayLinkAllotStrategy;

    private volatile Map<Long, NetRelayServeInstance> instanceMap = new ConcurrentHashMap<>();

    private volatile List<NetRelayServeInstance> instances = ImmutableList.of();

    private volatile List<NetRelayServeInstance> healthyInstances = ImmutableList.of();

    public AtomicBoolean close = new AtomicBoolean(false);

    @Override
    public RelayServeInstance getLocalInstance(long id) {
        return instanceMap.get(id);
    }

    public NetRelayServeInstance instanceOf(long id) {
        return instanceMap.get(id);
    }

    protected List<NetRelayServeInstance> instances() {
        return instances;
    }

    @Override
    public List<ServeInstance> getInstances() {
        return as(instances);
    }

    @Override
    public List<RelayServeInstance> getHealthyLocalInstances() {
        return as(healthyInstances);
    }

    public BaseRemoteServeCluster(String serveName, String service, String username, ServeInstanceAllotStrategy instanceAllotStrategy,
            RelayLinkAllotStrategy relayLinkAllotStrategy) {
        this.serveName = serveName;
        this.username = username;
        this.service = service;
        this.instanceAllotStrategy = instanceAllotStrategy;
        this.relayLinkAllotStrategy = relayLinkAllotStrategy;
    }

    @Override
    public String getServeName() {
        return serveName;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isClose() {
        return close.get();
    }

    @Override
    public ClientRelayLink allotLink(Tunnel tunnel) {
        RelayServeInstance instance = instanceAllotStrategy.allot(tunnel, this);
        if (instance == null) {
            return null;
        }
        return relayLinkAllotStrategy.allot(tunnel, instance);
    }

    @Override
    public RelayServeInstance registerInstance(NetRelayServeInstance instance) {
        RelayServeInstance old = instanceMap.putIfAbsent(instance.getId(), instance);
        if (old == null) {
            this.doRefreshInstances();
        }
        return instance;
    }

    @Override
    public synchronized void unregisterInstance(long instanceId) {
        NetRelayServeInstance instance = instanceMap.remove(instanceId);
        if (instance != null && !instance.isClose()) {
            instance.close();
            this.doRefreshInstances();
        }
    }

    @Override
    public void updateInstance(ServeNode node) {
        NetRelayServeInstance instance = instanceMap.get(node.getId());
        if (instance != null) {
            instance.updateMetadata(node.getMetadata());
            if (instance.updateHealthy(node.isHealthy())) {
                this.refreshInstances();
            }
        }
    }

    @Override
    public synchronized void refreshInstances() {
        this.doRefreshInstances();
    }

    private void doRefreshInstances() {
        this.instances = ImmutableList.sortedCopyOf(instanceMap.values());
        this.healthyInstances = ImmutableList.copyOf(instances.stream().filter(NetRelayServeInstance::isHealthy).collect(Collectors.toList()));
    }

    @Override
    public synchronized void close() {
        if (close.compareAndSet(false, true)) {
            List<NetRelayServeInstance> oldList = instances;
            instances = ImmutableList.of();
            oldList.forEach(NetRelayServeInstance::close);
            instanceMap = new ConcurrentHashMap<>();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BaseRemoteServeCluster)) {
            return false;
        }

        BaseRemoteServeCluster that = (BaseRemoteServeCluster) o;

        return new EqualsBuilder().append(getServeName(), that.getServeName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getServeName()).toHashCode();
    }

}
