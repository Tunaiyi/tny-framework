package com.tny.game.codec.configuration;

import com.tny.game.codec.protobuf.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 8:58 下午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ProtobufObjectCodecFactory.class)
public class JProtobufObjectCodecAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ProtobufObjectCodecFactory.class)
	public ProtobufObjectCodecFactory protobufObjectCodecorFactory() {
		return new ProtobufObjectCodecFactory();
	}

}
