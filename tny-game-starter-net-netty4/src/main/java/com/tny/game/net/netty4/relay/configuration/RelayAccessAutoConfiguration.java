package com.tny.game.net.netty4.relay.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.netty4.relay.cluster.*;
import com.tny.game.net.netty4.relay.guide.*;
import com.tny.game.net.netty4.relay.router.*;
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
@Configuration
@EnableConfigurationProperties({
		FixedRelayMessageRoutersProperties.class,
		SpringRelayServeClustersProperties.class,
		SpringBootRelayBootstrapProperties.class
})
public class RelayAccessAutoConfiguration {

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
		return new FixedRelayMessageRouter(properties.getClusterId());
	}

	@Bean
	@ConditionalOnMissingBean(RelayMessageRouter.class)
	public RelayMessageRouter firstRelayMessageRouter() {
		return new FirstRelayMessageRouter();
	}

	@Bean
	public LocalRelayContext localRelayContext(
			NetAppContext appContext, RelayMessageRouter relayMessageRouter, ServeClusterFilter serveClusterFilter) {
		return new NettyLocalRelayContext(appContext, relayMessageRouter, serveClusterFilter);
	}

	@Bean
	public LocalRelayExplorer localRelayExplorer(LocalRelayContext localRelayContext, List<NettyLocalServeClusterContext> clusterContexts) {
		return new NettyLocalRelayExplorer(localRelayContext, clusterContexts);
	}

	@Bean
	public LocalRelayTunnelFactory localRelayTunnelFactory(LocalRelayExplorer localRelayExplorer) {
		return new LocalRelayTunnelFactory(localRelayExplorer);
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
