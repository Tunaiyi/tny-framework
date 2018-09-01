package com.tny.game.net.session;

import com.tny.game.common.config.Config;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.utils.NetConfigs;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.tunnel.NetTunnel;

@Unit("CommonSessionFactory")
public class SingleTunnelSessionFactory<UID> implements SessionFactory<UID> {

    private AppConfiguration appConfiguration;

    private UID unloginUID;

    private int cacheMessageSize;

    public SingleTunnelSessionFactory(UID unloginUID, AppConfiguration appConfiguration) {
        this.unloginUID = unloginUID;
        this.appConfiguration = appConfiguration;
        Config config = appConfiguration.getProperties();
        this.cacheMessageSize = config.getInt(NetConfigs.SESSION_CACHE_MESSAGE_SIZE, 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public NetSession<UID> createSession(NetTunnel<UID> tunnel) {
        return new SingleTunnelSession<>(tunnel, this.unloginUID,
                appConfiguration.getInputEventHandler(),
                appConfiguration.getOutputEventHandler(),
                cacheMessageSize);
    }

}
