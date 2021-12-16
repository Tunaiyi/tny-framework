package com.tny.game.net.netty4.relay.configuration;

import com.tny.game.net.base.*;
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
		SpringRelayServeClustersProperties.class,
		SpringBootRelayBootstrapProperties.class
})
public class RemoteRelayAutoConfiguration {

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
	public RemoteRelayContext remoteRelayContext(
			NetAppContext appContext, RelayMessageRouter relayMessageRouter, ServeClusterFilter serveClusterFilter) {
		return new NettyRemoteRelayContext(appContext, relayMessageRouter, serveClusterFilter);
	}

	@Bean
	@ConditionalOnMissingBean(NoopServeNodeClient.class)
	public NoopServeNodeClient serveNodeClient() {
		return new NoopServeNodeClient();
	}

	@Bean
	public RemoteRelayServeNodeWatchService remoteRelayServeNodeWatchService(
			ServeNodeClient serveNodeClient, NetRemoteRelayExplorer remoteRelayExplorer) {
		return new RemoteRelayServeNodeWatchService(serveNodeClient, remoteRelayExplorer);
	}

	@Bean
	public NettyRemoteRelayExplorer remoteRelayExplorer(RemoteRelayContext remoteRelayContext,
			List<NettyRemoteServeClusterContext> clusterContexts) {
		return new NettyRemoteRelayExplorer(remoteRelayContext, clusterContexts);
	}

	@Bean
	public RelayTunnelFactory relayTunnelFactory(RemoteRelayExplorer remoteRelayExplorer) {
		return new RelayTunnelFactory(remoteRelayExplorer);
	}

	@Bean
	public RelayNettyMessageHandler relayNettyMessageHandler() {
		return new RelayNettyMessageHandler();
	}

	@Bean
	@ConditionalOnMissingBean(ServeClusterFilter.class)
	public AllRequiredServeClusterFilter allRequiredServeClusterFilter() {
		return new AllRequiredServeClusterFilter();
	}

}
