package com.tny.game.net.netty4.rpc.configuration;

import com.tny.game.net.base.*;
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
    public RpcRemoteRouter firstRpcRemoteRouter() {
        return new FirstRpcRemoteRouter();
    }

    @Bean
    public EndpointRemoteRouter endpointRemoteRouter() {
        return new EndpointRemoteRouter();
    }

    @Bean
    public RpcRemoterManager defaultRpcRemoteServiceManager(RpcRemoteProperties properties) {
        return new DefaultRpcRemoterManager(properties.getClient());
    }

    @Bean
    public RpcRemoteRouteManager rpcRouteManager(RpcRemoteSetting setting, ObjectProvider<RpcRemoteRouter> rpcRoutersProvider) {
        return new DefaultRpcRemoteRouteManager(setting.getDefaultRpcRemoteRouter(), rpcRoutersProvider.stream().collect(Collectors.toList()));
    }

    @Bean
    public RpcRemoteInstanceFactory rpcInstanceFactory(RpcRemoteSetting setting, RpcRemoterManager service, RpcRemoteRouteManager manager) {
        return new RpcRemoteInstanceFactory(setting, service, manager);
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
    public RpcForwarder defaultRpcForwarder(RpcForwardManager forwardManager, List<RpcServiceForwardStrategy> strategies) {
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
