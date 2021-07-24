package com.tny.game.common.codec.configuration;

import com.tny.game.common.codec.typeprotobuf.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 8:58 下午
 */
@Configuration
@ConditionalOnClass(TypeProtobufObjectCodecFactory.class)
public class TypeProtobufObjectCodecAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TypeProtobufObjectCodecFactory.class)
    public TypeProtobufObjectCodecFactory typeProtobufObjectCodecFactory() {
        return new TypeProtobufObjectCodecFactory();
    }

}
