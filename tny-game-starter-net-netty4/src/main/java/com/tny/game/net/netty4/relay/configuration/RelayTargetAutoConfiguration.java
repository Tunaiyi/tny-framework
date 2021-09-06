package com.tny.game.net.netty4.relay.configuration;

import com.tny.game.net.netty4.relay.guide.*;
import com.tny.game.net.relay.link.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableConfigurationProperties({
		SpringBootRelayBootstrapProperties.class,
})
public class RelayTargetAutoConfiguration {

	@Bean
	public RemoteRelayExplorer remoteRelayExplorer() {
		return new DefaultRemoteRelayExplorer();
	}

}
