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
package com.tny.game.net.netty4.rpc.configuration;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.rpc.service.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.rpc.auth.*;
import com.tny.game.net.transport.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@Import({
        ImportRpcServiceDefinitionRegistrar.class,
        ImportRpcClientDefinitionRegistrar.class
})
@EnableConfigurationProperties(RpcClusterProperties.class)
public class RpcAutoConfiguration {

    @Bean
    public FirstRpcRouter firstRpcRouter() {
        return new FirstRpcRouter();
    }

    @Bean
    public RpcAccessRouter accessRouter() {
        return new RpcAccessRouter();
    }

    @Bean
    public RpcRouteManager rpcRouteManager(RpcRemoteSetting setting, ObjectProvider<RpcRouter> rpcRoutersProvider) {
        return new DefaultRpcRouteManager(setting.getDefaultRpcRemoteRouter(), rpcRoutersProvider.stream().collect(Collectors.toList()));
    }

    @Bean
    @ConditionalOnBean(RpcMonitor.class)
    public RpcRemoteInstanceFactory rpcInstanceFactory(
            RpcRemoteSetting setting, RpcInvokeNodeManager remoterManager, RpcRouteManager routeManager, RpcMonitor rpcMonitor) {
        return new RpcRemoteInstanceFactory(setting, remoterManager, routeManager, rpcMonitor);
    }

    @Bean
    public RpcServeNodeWatchService rpcServeNodeWatchService(
            @Autowired(required = false) ServeNodeClient client,
            ObjectProvider<RpcConnectorFactory> connectorProvider) {
        return new RpcServeNodeWatchService(client, connectorProvider.stream().collect(Collectors.toList()));
    }

    @Bean
    @ConditionalOnMissingBean(RpcAuthController.class)
    public RpcAuthController rpcAuthController() {
        return new RpcAuthController();
    }

    @Bean
    @ConditionalOnMissingBean(RpcUserPasswordManager.class)
    public RpcUserPasswordManager rpcUserPasswordManager() {
        return new NoopRpcUserPasswordManager();
    }

    @Bean
    public RpcServicerManager rpcServicerManager(RpcClusterProperties properties) {
        return new DefaultRpcServicerManager(properties);
    }

    @Bean
    public RpcForwarder defaultRpcForwarder(RpcForwardNodeManager forwardManager, List<RpcServiceForwardStrategy> strategies) {
        return new DefaultRpcForwarder(forwardManager, new FirstRpcForwarderStrategy(), strategies);
    }

    @Bean
    @ConditionalOnBean(NetAppContext.class)
    @ConditionalOnMissingBean(RpcAuthService.class)
    public RpcAuthService rpcAuthService(NetAppContext netAppContext, RpcUserPasswordManager rpcUserPasswordManager) {
        return new DefaultRpcAuthService(netAppContext, rpcUserPasswordManager);
    }

    @Bean
    public RpcPasswordValidator PasswordValidator(RpcAuthService rpcAuthService) {
        return new RpcPasswordValidator(rpcAuthService);
    }

    @Bean
    public RpcTokenValidator tokenValidator(RpcAuthService rpcAuthService) {
        return new RpcTokenValidator(rpcAuthService);
    }

}
