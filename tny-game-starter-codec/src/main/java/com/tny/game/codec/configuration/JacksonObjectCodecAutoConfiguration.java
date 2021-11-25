package com.tny.game.codec.configuration;

import com.tny.game.codec.jackson.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 8:58 下午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JacksonObjectCodecFactory.class)
public class JacksonObjectCodecAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(JacksonObjectCodecFactory.class)
	public JacksonObjectCodecFactory jacksonObjectCodecorFactory() {
		return new JacksonObjectCodecFactory();
	}

}
