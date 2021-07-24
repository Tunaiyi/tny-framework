package com.tny.game.net.netty4.configuration.jprotobuf;

import com.tny.game.net.message.codec.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 8:42 下午
 */
@Configuration
@ConditionalOnClass({JProtobufMessageBodyCodec.class})
public class JProtobufAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JProtobufMessageBodyCodec.class)
    public MessageBodyCodec<Object> jprotobufMessageBodyCodec() {
        return new JProtobufMessageBodyCodec<>();
    }

}
