package com.tny.game.net.common.session;

import com.tny.game.common.config.Config;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.AppConstants;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.SessionFactory;
import com.tny.game.net.tunnel.NetTunnel;

@Unit("CommonSessionFactory")
public class CommonSessionFactory<UID> implements SessionFactory<UID> {

    private AppConfiguration appConfiguration;

    private UID unloginUID;

    private int cacheMessageSize;

    public CommonSessionFactory(UID unloginUID, AppConfiguration appConfiguration) {
        this.unloginUID = unloginUID;
        this.appConfiguration = appConfiguration;
        Config config = appConfiguration.getProperties();
        this.cacheMessageSize = config.getInt(AppConstants.SESSION_CACHE_MESSAGE_SIZE, 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public NetSession<UID> createSession(NetTunnel<UID> tunnel) {
        return new CommonSession<>(tunnel, this.unloginUID,
                appConfiguration.getOutputEventHandler(),
                appConfiguration.getInputEventHandler(),
                cacheMessageSize);
    }

}
