package com.tny.game.net.netty4.cloud.nacos.configuration;

import com.alibaba.cloud.nacos.*;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.tny.game.net.netty4.cloud.nacos.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 1:29 下午
 */
@Configuration
@ConditionalOnClass(NacosDiscoveryProperties.class)
@AutoConfigureAfter(NacosDiscoveryAutoConfiguration.class)
public class NetNacosDiscoveryClientConfiguration {

	@Bean
	@ConditionalOnBean(NacosDiscoveryProperties.class)
	public ServeNodeClient serveNodeClient(NacosDiscoveryProperties properties, NacosServiceManager nacosServiceManager) {
		return new NacosServeNodeClient(properties, nacosServiceManager);
	}

	@Bean
	@ConditionalOnBean(NetLocalRelayExplorer.class)
	public ServeNodeWatchService serveNodeWatchService(ServeNodeClient serveNodeClient, NetLocalRelayExplorer localRelayExplorer) {
		return new ServeNodeWatchService(serveNodeClient, localRelayExplorer);
	}

}
