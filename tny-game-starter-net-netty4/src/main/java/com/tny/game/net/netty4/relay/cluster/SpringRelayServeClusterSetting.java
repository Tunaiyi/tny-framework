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

package com.tny.game.net.netty4.relay.cluster;

import com.google.common.collect.Lists;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.relay.link.allot.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.tny.game.common.lifecycle.unit.UnitNames.*;

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
        // 开关语义自洽：serveName 只是发现键，不得反向强制开启发现（显式 false 必须生效）
        return this.discovery;
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
