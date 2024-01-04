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

import com.google.common.collect.Lists;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.relay.link.allot.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.tny.game.net.application.configuration.NetUnitNames.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 12:26 下午
 */
public class SpringRelayServeClusterSetting implements RelayServeClusterSetting {

    private String serveName;

    private String service;

    private String username;

    private String clientGuide = defaultName(RelayClientGuide.class);

    private int connectionSize = 1;

    private long connectionHeartbeatInterval = 5000;

    private long connectionMaxIdleTime = 10000;

    private boolean discovery = true;

    private String serveInstanceAllotStrategy = lowerCamelName(PollingRelayAllotStrategy.class);

    private String relayLinkAllotStrategy = lowerCamelName(PollingRelayAllotStrategy.class);

    private List<SpringRelayServeNodeSetting> serveNodes = new ArrayList<>();

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getServeName() {
        return serveName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getClientGuide() {
        return clientGuide;
    }

    @Override
    public boolean isDiscovery() {
        return discovery || StringUtils.isNoneBlank(this.serveName);
    }

    @Override
    public long getConnectionHeartbeatInterval() {
        return connectionHeartbeatInterval;
    }

    @Override
    public long getConnectionMaxIdleTime() {
        return connectionMaxIdleTime;
    }

    @Override
    public int getConnectionSize() {
        return connectionSize;
    }

    public List<SpringRelayServeNodeSetting> getServeNodes() {
        return serveNodes;
    }

    @Override
    public List<RelayServeNodeSetting> getServeNodeList() {
        return Lists.newArrayList(serveNodes);
    }

    public SpringRelayServeClusterSetting setUsername(String username) {
        this.username = username;
        return this;
    }

    public boolean isHasServeInstanceAllotStrategy() {
        return StringUtils.isNoneBlank(serveInstanceAllotStrategy);
    }

    public String getServeInstanceAllotStrategy() {
        return serveInstanceAllotStrategy;
    }

    public boolean isHasRelayLinkAllotStrategy() {
        return StringUtils.isNoneBlank(relayLinkAllotStrategy);
    }

    public String getRelayLinkAllotStrategy() {
        return relayLinkAllotStrategy;
    }

    public SpringRelayServeClusterSetting setService(String service) {
        this.service = service;
        return this;
    }

    public SpringRelayServeClusterSetting setServeName(String serveName) {
        this.serveName = serveName;
        return this;
    }

    public SpringRelayServeClusterSetting setDiscovery(boolean discovery) {
        this.discovery = discovery;
        return this;
    }

    public SpringRelayServeClusterSetting setClientGuide(String clientGuide) {
        this.clientGuide = clientGuide;
        return this;
    }

    public SpringRelayServeClusterSetting setConnectionSize(int connectionSize) {
        this.connectionSize = connectionSize;
        return this;
    }

    public SpringRelayServeClusterSetting setServeInstanceAllotStrategy(String serveInstanceAllotStrategy) {
        this.serveInstanceAllotStrategy = serveInstanceAllotStrategy;
        return this;
    }

    public SpringRelayServeClusterSetting setRelayLinkAllotStrategy(String relayLinkAllotStrategy) {
        this.relayLinkAllotStrategy = relayLinkAllotStrategy;
        return this;
    }

    public SpringRelayServeClusterSetting setServeNodes(List<SpringRelayServeNodeSetting> serveNodes) {
        this.serveNodes = serveNodes;
        return this;
    }

    public SpringRelayServeClusterSetting setConnectionMaxIdleTime(long connectionMaxIdleTime) {
        this.connectionMaxIdleTime = connectionMaxIdleTime;
        return this;
    }

    public SpringRelayServeClusterSetting setConnectionHeartbeatInterval(long connectionHeartbeatInterval) {
        this.connectionHeartbeatInterval = connectionHeartbeatInterval;
        return this;
    }

}
