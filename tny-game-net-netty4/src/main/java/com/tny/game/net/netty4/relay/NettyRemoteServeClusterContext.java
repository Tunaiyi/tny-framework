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
