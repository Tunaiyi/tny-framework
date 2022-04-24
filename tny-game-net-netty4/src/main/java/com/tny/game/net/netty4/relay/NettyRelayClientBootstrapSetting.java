package com.tny.game.net.netty4.relay;

import com.tny.game.net.base.configuration.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 1:50 下午
 */
public class NettyRelayClientBootstrapSetting extends CommonClientBootstrapSetting implements NettyRelayBootstrapSetting {

    private NettyRelayChannelSetting channel;

    public NettyRelayClientBootstrapSetting() {
    }

    public NettyRelayClientBootstrapSetting(NettyRelayChannelSetting channel) {
        this.channel = channel;
    }

    @Override
    public NettyRelayChannelSetting getChannel() {
        return channel;
    }

    public NettyRelayClientBootstrapSetting setChannel(NettyRelayChannelSetting channel) {
        this.channel = channel;
        return this;
    }

}
