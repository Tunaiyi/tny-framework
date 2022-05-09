package com.tny.game.net.netty4.network;

import com.tny.game.net.base.configuration.*;
import com.tny.game.net.netty4.*;

public class NettyNetClientBootstrapSetting extends CommonClientBootstrapSetting implements NettyBootstrapSetting {

    private NettyChannelSetting channel;

    public NettyNetClientBootstrapSetting() {
        this.channel = new NettyChannelSetting(null, null);
    }

    public NettyNetClientBootstrapSetting(NettyChannelSetting channel) {
        this.channel = channel;
    }

    public NettyNetClientBootstrapSetting(String name) {
        this.setName(name);
    }

    @Override
    public NettyChannelSetting getChannel() {
        return channel;
    }

    public NettyNetClientBootstrapSetting setChannel(NettyChannelSetting channel) {
        this.channel = channel;
        return this;
    }

}
