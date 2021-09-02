package com.tny.game.net.netty4.relay.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.datagram.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.netty4.relay.cluster.*;
import com.tny.game.net.netty4.relay.guide.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.allot.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableConfigurationProperties({
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
	public LocalRelayExplorer localRelayExplorer(NetAppContext appContext, List<RelayServeClusterContext> clusterContexts) {
		return new NettyLocalRelayExplorer(appContext, clusterContexts);
	}

	@Bean
	public LocalRelayTunnelFactory localRelayTunnelFactory(ServerTunnelFactory serverTunnelFactory, LocalRelayExplorer localRelayExplorer) {
		return new LocalRelayTunnelFactory(serverTunnelFactory, localRelayExplorer);
	}

}
