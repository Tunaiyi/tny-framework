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
package com.tny.game.net.netty4.rpc.configuration;

import com.tny.game.net.base.*;
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
        ImportRpcConnectorDefinitionRegistrar.class
})
@EnableConfigurationProperties(RpcRemoteProperties.class)
public class RpcAutoConfiguration {

    @Bean
    public FirstRpcRouter firstRpcRouter() {
        return new FirstRpcRouter();
    }

    @Bean
    public EndpointRouter endpointRouter() {
        return new EndpointRouter();
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
            ObjectProvider<RpcClientFactory> connectorProvider) {
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
    public RpcServicerManager rpcServicerManager(RpcRemoteProperties properties) {
        return new DefaultRpcServicerManager(properties.getClient());
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
