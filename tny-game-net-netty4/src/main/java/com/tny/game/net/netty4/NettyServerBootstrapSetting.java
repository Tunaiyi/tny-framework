package com.tny.game.net.netty4;

import com.tny.game.net.base.configuration.*;

public class NettyServerBootstrapSetting extends CommonServerBootstrapSetting implements NettyBootstrapSetting {

    private NettyChannelMakerSetting channelMaker = new NettyChannelMakerSetting();

    public NettyServerBootstrapSetting() {
    }

    public NettyServerBootstrapSetting(String name) {
        this.setName(name);
    }

    public NettyChannelMakerSetting getChannelMaker() {
        return this.channelMaker;
    }

    public NettyServerBootstrapSetting setChannelMaker(NettyChannelMakerSetting channelMaker) {
        this.channelMaker = channelMaker;
        return this;
    }

}
