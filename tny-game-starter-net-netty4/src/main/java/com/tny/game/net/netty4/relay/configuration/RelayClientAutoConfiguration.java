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
package com.tny.game.net.netty4.relay.configuration;

import com.tny.game.net.application.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.netty4.relay.cluster.*;
import com.tny.game.net.netty4.relay.guide.*;
import com.tny.game.net.netty4.relay.router.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.allot.*;
import com.tny.game.net.relay.link.route.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        FixedRelayMessageRoutersProperties.class,
        SpringRelayClustersProperties.class,
        SpringBootRelayBootstrapProperties.class
})
public class RelayClientAutoConfiguration {

    @Bean
    public PollingRelayAllotStrategy pollingRelayAllotStrategy() {
        return new PollingRelayAllotStrategy();
    }

    @Bean
    public RandomRelayAllotStrategy randomRelayAllotStrategy() {
        return new RandomRelayAllotStrategy();
    }

    @Bean
    @ConditionalOnProperty(value = "tny.net.relay.router.fixed-message-router.cluster-id")
    public RelayMessageRouter fixedRelayMessageRouter(FixedRelayMessageRoutersProperties properties) {
        return new FixedRelayMessageRouter(properties.getService());
    }

    @Bean
    @ConditionalOnMissingBean(RelayMessageRouter.class)
    public RelayMessageRouter firstRelayMessageRouter() {
        return new FirstRelayMessageRouter();
    }

    @Bean
    @ConditionalOnBean(NetAppContext.class)
    public ClientRelayContext remoteRelayContext(
            NetAppContext appContext, RelayMessageRouter relayMessageRouter, ServeClusterFilter serveClusterFilter) {
        return new NettyClientRelayContext(appContext, relayMessageRouter, serveClusterFilter);
    }

    @Bean
    @ConditionalOnMissingBean(ServeNodeClient.class)
    public ServeNodeClient noopServeNodeClient() {
        return new NoopServeNodeClient();
    }

    @Bean
    public RelayRemoteServeNodeWatchService remoteRelayServeNodeWatchService(
            ServeNodeClient serveNodeClient, NetClientRelayExplorer remoteRelayExplorer) {
        return new RelayRemoteServeNodeWatchService(serveNodeClient, remoteRelayExplorer);
    }

    @Bean
    public NettyClientRelayExplorer clientRelayExplorer(ClientRelayContext remoteRelayContext,
            List<NettyRemoteServeClusterContext> clusterContexts) {
        return new NettyClientRelayExplorer(remoteRelayContext, clusterContexts);
    }

    @Bean
    public ClientRelayTunnelFactory relayTunnelFactory(ClientRelayExplorer remoteRelayExplorer) {
        return new ClientRelayTunnelFactory(remoteRelayExplorer);
    }

    @Bean
    public NettyMessageHandlerFactory relayMessageHandlerFactory() {
        return new NettyRelayMessageHandlerFactory();
    }

    @Bean
    @ConditionalOnMissingBean(ServeClusterFilter.class)
    public AllRequiredServeClusterFilter allRequiredServeClusterFilter() {
        return new AllRequiredServeClusterFilter();
    }

}
