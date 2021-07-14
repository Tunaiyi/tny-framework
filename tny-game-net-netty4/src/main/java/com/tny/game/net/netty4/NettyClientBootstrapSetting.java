package com.tny.game.net.netty4;

import com.tny.game.net.base.configuration.*;

public class NettyClientBootstrapSetting extends CommonClientBootstrapSetting implements NettyBootstrapSetting {

    private NettyChannelMakerSetting channelMaker = new NettyChannelMakerSetting();

    public NettyClientBootstrapSetting() {

    }

    public NettyClientBootstrapSetting(String name) {
        this.setName(name);
    }

    public NettyChannelMakerSetting getChannelMaker() {
        return this.channelMaker;
    }

    public NettyClientBootstrapSetting setChannelMaker(NettyChannelMakerSetting channelMaker) {
        this.channelMaker = channelMaker;
        return this;
    }

}
