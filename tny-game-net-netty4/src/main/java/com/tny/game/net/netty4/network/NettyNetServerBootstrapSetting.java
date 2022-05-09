package com.tny.game.net.netty4.network;

import com.tny.game.net.base.configuration.*;
import com.tny.game.net.netty4.*;

public class NettyNetServerBootstrapSetting extends CommonServerBootstrapSetting implements NettyBootstrapSetting {

    private NettyChannelSetting channel;

    public NettyNetServerBootstrapSetting() {
        this(null, null);
    }

    public NettyNetServerBootstrapSetting(String encodeBodyCodec, String decodeBodyCodec) {
        channel = new NettyChannelSetting(encodeBodyCodec, decodeBodyCodec);
    }

    public NettyNetServerBootstrapSetting(NettyChannelSetting channel) {
        this.channel = channel;
    }

    public NettyNetServerBootstrapSetting(String name) {
        this.setName(name);
    }

    @Override
    public NettyChannelSetting getChannel() {
        return channel;
    }

    public NettyNetServerBootstrapSetting setChannel(NettyChannelSetting channel) {
        this.channel = channel;
        return this;
    }

}
