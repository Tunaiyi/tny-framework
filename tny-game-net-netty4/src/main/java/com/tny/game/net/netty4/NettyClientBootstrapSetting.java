package com.tny.game.net.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.base.configuration.*;

import static com.tny.game.common.utils.ObjectAide.*;

public class NettyClientBootstrapSetting extends CommonClientBootstrapSetting implements NettyBootstrapSetting {

    private String channelMaker;

    public NettyClientBootstrapSetting(String name) {
        this.setName(name);
    }

    public NettyClientBootstrapSetting(NetAppContext appContext) {
        super(appContext);
    }

    @Override
    public String getChannelMaker() {
        return this.channelMaker;
    }

    @Override
    public NettyClientBootstrapSetting setName(String name) {
        super.setName(name);
        this.channelMaker = ifNullAndGet(this.channelMaker, () -> name + ChannelMaker.class.getSimpleName());
        return this;
    }

}
