package com.tny.game.net.demo.common;

import com.tny.game.net.message.codec.*;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
public class DemoAutoConfiguration {

    @Bean
    public JProtobufMessageBodyCodec<?> protobufBodyCodec() {
        return new JProtobufMessageBodyCodec<>();
    }

}
