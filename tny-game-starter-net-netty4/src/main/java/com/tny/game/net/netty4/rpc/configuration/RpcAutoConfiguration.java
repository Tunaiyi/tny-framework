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
@EnableConfigurationProperties(RpcProperties.class)
public class RpcAutoConfiguration {

    @Bean
    public RpcRouter<?> rpcRouter() {
        return new FirstRpcRouter();
    }

    @Bean
    public RpcRemoteServiceManager defaultRpcRemoteServiceManager(RpcProperties properties) {
        return new DefaultRpcRemoteServiceManager(properties.getClient());
    }

    @Bean
    public RpcRouteManager rpcRouteManager(ObjectProvider<RpcRouter<?>> rpcRoutersProvider) {
        return new DefaultRpcRouteManager(rpcRoutersProvider.stream().collect(Collectors.toList()));
    }

    @Bean
    public RpcInstanceFactory rpcInstanceFactory(RpcSetting setting, RpcRemoteServiceManager service, RpcRouteManager manager) {
        return new RpcInstanceFactory(setting, service, manager);
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
    public RpcForwarder defaultRpcForwarder(RpcRemoteServiceManager rpcRemoteServiceManager, List<RpcServiceForwarderStrategy> strategies) {
        return new DefaultRpcForwarder(rpcRemoteServiceManager, new FirstRpcForwarderStrategy(), strategies);
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
