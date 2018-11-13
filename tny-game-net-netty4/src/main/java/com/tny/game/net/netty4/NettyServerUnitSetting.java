package com.tny.game.net.netty4;

import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.configuration.CommonServerUnitSetting;

import static com.tny.game.common.utils.ObjectAide.*;

public class NettyServerUnitSetting extends CommonServerUnitSetting implements NettyUnitSetting {

    private String channelMaker;

    public NettyServerUnitSetting(String name) {
        this.setName(name);
    }

    public NettyServerUnitSetting(AppContext appContext) {
        super(appContext);
    }

    @Override
    public String getChannelMaker() {
        return this.channelMaker;
    }

    @Override
    public NettyServerUnitSetting setName(String name) {
        super.setName(name);
        this.channelMaker = ifNullAndGet(this.channelMaker, () -> name + ChannelMaker.class.getSimpleName());
        return this;
    }

}
