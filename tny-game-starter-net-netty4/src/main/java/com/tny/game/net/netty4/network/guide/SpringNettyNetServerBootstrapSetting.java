package com.tny.game.net.netty4.network.guide;

import com.tny.game.net.netty4.network.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
//@Order(HIGHEST_PRECEDENCE)
//@Configuration
public class SpringNettyNetServerBootstrapSetting extends NettyNetServerBootstrapSetting {

    @NestedConfigurationProperty
    private SpringNettyDatagramChannelSetting channel;

    public SpringNettyNetServerBootstrapSetting() {
        super(new SpringNettyDatagramChannelSetting());
    }

    @Override
    public SpringNettyDatagramChannelSetting getChannel() {
        return as(super.getChannel());
    }

    public SpringNettyNetServerBootstrapSetting setChannel(SpringNettyDatagramChannelSetting channel) {
        super.setChannel(channel);
        return this;
    }

}
