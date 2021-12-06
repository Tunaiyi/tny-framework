package com.tny.game.codec.configuration;

import com.fasterxml.jackson.databind.*;
import com.tny.game.codec.jackson.*;
import com.tny.game.codec.jackson.mapper.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

import java.util.stream.Collectors;

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

	@Bean
	public HandlerInstantiatorObjectMapperCustomizer handlerInstantiatorObjectMapperCustomizer(
			ObjectProvider<JsonSerializer<?>> jsonSerializerProviders,
			ObjectProvider<JsonDeserializer<?>> jsonDeserializerProviders,
			ObjectProvider<KeyDeserializer> keyDeserializerProviders) {
		return new HandlerInstantiatorObjectMapperCustomizer(
				jsonSerializerProviders.stream().collect(Collectors.toList()),
				jsonDeserializerProviders.stream().collect(Collectors.toList()),
				keyDeserializerProviders.stream().collect(Collectors.toList()));
	}

}
