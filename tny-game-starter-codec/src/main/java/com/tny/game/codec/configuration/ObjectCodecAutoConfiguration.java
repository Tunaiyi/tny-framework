package com.tny.game.codec.configuration;

import com.tny.game.codec.*;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 8:58 下午
 */
@Configuration(proxyBeanMethods = false)
public class ObjectCodecAutoConfiguration {

    @Bean
    public ObjectCodecAdapter objectCodecAdapter(List<ObjectCodecFactory> codecFactories) {
        return new ObjectCodecAdapter(codecFactories);
    }

    @Bean
    public ObjectCodecService objectCodecService(ObjectCodecAdapter objectCodecMatcher) {
        return new ObjectCodecService(objectCodecMatcher);
    }

}
