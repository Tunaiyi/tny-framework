package com.tny.game.starter.net.netty4.configuration.guide;

import com.tny.game.net.codec.v1.*;
import com.tny.game.net.netty4.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
//@Order(HIGHEST_PRECEDENCE)
//@Configuration
public class SpringNettyServerBootstrapSetting extends NettyServerBootstrapSetting {

    @NestedConfigurationProperty
    private NettyChannelMakerSetting channelMaker;

    @NestedConfigurationProperty
    private DataPacketV1Config encoder;

    @NestedConfigurationProperty
    private DataPacketV1Config decoder;

}
