package com.tny.game.common.codec.configuration;

import com.tny.game.common.codec.jackson.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 8:58 下午
 */
@Configuration
@ConditionalOnClass(JacksonObjectCodecFactory.class)
public class JacksonObjectCodecAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JacksonObjectCodecFactory.class)
    public JacksonObjectCodecFactory jacksonObjectCodecorFactory() {
        return new JacksonObjectCodecFactory();
    }

}
