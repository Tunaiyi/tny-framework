package com.tny.game.net.netty4;

import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.configuration.CommonClientUnitSetting;

import static com.tny.game.common.utils.ObjectAide.*;

public class NettyClientUnitSetting extends CommonClientUnitSetting implements NettyUnitSetting {

    private String channelMaker;

    public NettyClientUnitSetting(String name) {
        this.setName(name);
    }

    public NettyClientUnitSetting(AppContext appContext) {
        super(appContext);
    }

    @Override
    public String getChannelMaker() {
        return this.channelMaker;
    }


    @Override
    public NettyClientUnitSetting setName(String name) {
        super.setName(name);
        this.channelMaker = ifNullAndGet(this.channelMaker, () -> name + ChannelMaker.class.getSimpleName());
        return this;
    }

}
