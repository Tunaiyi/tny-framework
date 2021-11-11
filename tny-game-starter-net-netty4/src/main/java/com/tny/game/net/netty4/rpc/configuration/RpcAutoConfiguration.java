package com.tny.game.net.netty4.rpc.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.rpc.service.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.rpc.auth.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.stream.Collectors;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
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
	public RpcRemoteService rpcRemoteService(RpcProperties properties) {
		return new DefaultRpcRemoteService(properties.getClient());
	}

	@Bean
	public RpcRouteManager rpcRouteManager(ObjectProvider<RpcRouter<?>> rpcRoutersProvider) {
		return new DefaultRpcRouteManager(rpcRoutersProvider.stream().collect(Collectors.toList()));
	}

	@Bean
	public RpcInstanceFactory rpcInstanceFactory(RpcSetting setting, RpcRemoteService service, RpcRouteManager manager) {
		return new RpcInstanceFactory(setting, service, manager);
	}

	@Bean
	public RpcServeNodeWatchService rpcServeNodeWatchService(
			@Autowired(required = false) ServeNodeClient client,
			ObjectProvider<RpcClientFactory> connectorProvider) {
		return new RpcServeNodeWatchService(client, connectorProvider.stream().collect(Collectors.toList()));
	}

	@Bean
	public RpcAuthController rpcAuthController() {
		return new RpcAuthController();
	}

	@Bean
	@ConditionalOnMissingBean(RpcUserPasswordManager.class)
	public RpcUserPasswordManager rpcUserPasswordManager() {
		return new NoopRpcUserPasswordManager();
	}

	@Bean
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
