package com.tny.game.net.netty4;

import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.configuration.CommonServerBootstrapSetting;

import static com.tny.game.common.utils.ObjectAide.*;

public class NettyServerBootstrapSetting extends CommonServerBootstrapSetting implements NettyBootstrapSetting {

    private String channelMaker;

    public NettyServerBootstrapSetting(String name) {
        this.setName(name);
    }

    public NettyServerBootstrapSetting(AppContext appContext) {
        super(appContext);
    }

    @Override
    public String getChannelMaker() {
        return this.channelMaker;
    }

    @Override
    public NettyServerBootstrapSetting setName(String name) {
        super.setName(name);
        this.channelMaker = ifNullAndGet(this.channelMaker, () -> name + ChannelMaker.class.getSimpleName());
        return this;
    }

}
