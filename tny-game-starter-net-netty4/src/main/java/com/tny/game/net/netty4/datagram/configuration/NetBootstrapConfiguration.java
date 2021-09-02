package com.tny.game.net.netty4.datagram.configuration;

import com.tny.game.net.netty4.datagram.guide.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableConfigurationProperties({
		SpringBootNetBootstrapProperties.class,
})
public class NetBootstrapConfiguration {

}
