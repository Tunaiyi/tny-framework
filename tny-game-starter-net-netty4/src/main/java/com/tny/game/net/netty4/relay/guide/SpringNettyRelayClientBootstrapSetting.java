package com.tny.game.net.netty4.relay.guide;

import com.tny.game.net.netty4.relay.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 3:56 下午
 */
public class SpringNettyRelayClientBootstrapSetting extends NettyRelayClientBootstrapSetting {

    @NestedConfigurationProperty
    private SpringNettyRelayChannelSetting channel;

    public SpringNettyRelayClientBootstrapSetting() {
        super(new SpringNettyRelayChannelSetting());
    }

    @Override
    public SpringNettyRelayChannelSetting getChannel() {
        return as(super.getChannel());
    }

    public NettyRelayClientBootstrapSetting setChannel(SpringNettyRelayChannelSetting channel) {
        return super.setChannel(channel);
    }

}

