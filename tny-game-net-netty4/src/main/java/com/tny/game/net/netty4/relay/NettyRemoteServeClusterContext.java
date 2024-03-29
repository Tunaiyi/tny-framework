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

import com.tny.game.common.url.*;
import com.tny.game.net.clusters.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.allot.*;

import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public class NettyRemoteServeClusterContext implements RemoteServeClusterContext {

    private final RelayServeClusterSetting setting;

    private RelayClientGuide clientGuide;

    private ServeInstanceAllotStrategy serveInstanceAllotStrategy = new PollingRelayAllotStrategy();

    private RelayLinkAllotStrategy relayLinkAllotStrategy = new PollingRelayAllotStrategy();

    public NettyRemoteServeClusterContext(RelayServeClusterSetting setting) {
        this.setting = setting;
    }

    @Override
    public String getService() {
        return setting.serviceName();
    }

    @Override
    public String getServeName() {
        return setting.discoverService();
    }

    @Override
    public String getUsername() {
        return setting.getUsername();
    }

    @Override
    public RelayServeClusterSetting getSetting() {
        return setting;
    }

    public RelayClientGuide getClientGuide() {
        return clientGuide;
    }


    @Override
    public List<NetAccessNode> getInstances() {
        return as(setting.getServeNodeList());
    }

    @Override
    public ServeInstanceAllotStrategy getServeInstanceAllotStrategy() {
        return serveInstanceAllotStrategy;
    }

    @Override
    public RelayLinkAllotStrategy getRelayLinkAllotStrategy() {
        return relayLinkAllotStrategy;
    }

    /**
     * @param url url
     */
    @Override
    public void connect(URL url, RelayConnectCallback callback) {
        clientGuide.connect(url, callback);
    }

    public NettyRemoteServeClusterContext setClientGuide(RelayClientGuide clientGuide) {
        this.clientGuide = clientGuide;
        return this;
    }

    public NettyRemoteServeClusterContext setServeInstanceAllotStrategy(
            ServeInstanceAllotStrategy serveInstanceAllotStrategy) {
        this.serveInstanceAllotStrategy = serveInstanceAllotStrategy;
        return this;
    }

    public NettyRemoteServeClusterContext setRelayLinkAllotStrategy(RelayLinkAllotStrategy relayLinkAllotStrategy) {
        this.relayLinkAllotStrategy = relayLinkAllotStrategy;
        return this;
    }

}
