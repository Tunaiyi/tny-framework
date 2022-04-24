package com.tny.game.codec.configuration;

import com.tny.game.codec.typeprotobuf.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 8:58 下午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(TypeProtobufObjectCodecFactory.class)
public class TypeProtobufObjectCodecAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TypeProtobufObjectCodecFactory.class)
    public TypeProtobufObjectCodecFactory typeProtobufObjectCodecFactory() {
        return new TypeProtobufObjectCodecFactory();
    }

}
