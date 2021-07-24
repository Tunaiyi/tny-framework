package com.tny.game.common.codec.configuration;

import com.tny.game.common.codec.*;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 8:58 下午
 */
@Configuration
public class ObjectCodecAutoConfiguration {

    @Bean
    public ObjectCodecService objectCodecService(List<ObjectCodecorFactory> codecorFactories) {
        return new ObjectCodecService(codecorFactories);
    }

}
