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
    public ObjectCodecService objectCodecService(List<ObjectCodecFactory> codecorFactories) {
        return new ObjectCodecService(codecorFactories);
    }

}
