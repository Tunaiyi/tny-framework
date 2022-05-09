package com.tny.game.net.netty4.network.guide;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.network.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 只是为了生成配置说明
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
public class SpringNettyNetClientBootstrapSetting extends NettyNetClientBootstrapSetting {

    @NestedConfigurationProperty
    private SpringNettyChannelSetting channel;

    @NestedConfigurationProperty
    private ClientConnectorSetting connector;

    public SpringNettyNetClientBootstrapSetting() {
        super(new SpringNettyChannelSetting());
    }

    @Override
    public SpringNettyChannelSetting getChannel() {
        return as(super.getChannel());
    }

    public NettyNetClientBootstrapSetting setChannel(SpringNettyChannelSetting channel) {
        return super.setChannel(channel);
    }

    @Override
    public ClientConnectorSetting getConnector() {
        return super.getConnector();
    }

    @Override
    public NettyNetClientBootstrapSetting setConnector(ClientConnectorSetting connector) {
        super.setConnector(connector);
        return this;
    }

}
