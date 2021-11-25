package com.tny.game.net.netty4.network.configuration;

import com.tny.game.net.netty4.network.guide.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
		SpringBootNetBootstrapProperties.class,
})
public class NetBootstrapConfiguration {

}
