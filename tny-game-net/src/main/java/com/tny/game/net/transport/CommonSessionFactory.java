package com.tny.game.net.transport;

import com.tny.game.common.config.Config;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.utils.NetConfigs;

@Unit("CommonSessionFactory")
public class CommonSessionFactory<UID> implements SessionFactory<UID> {

    private int cacheMessageSize;

    public CommonSessionFactory(AppConfiguration appConfiguration) {
        Config config = appConfiguration.getProperties();
        this.cacheMessageSize = config.getInt(NetConfigs.SESSION_CACHE_MESSAGE_SIZE, 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public NetSession<UID> createSession(Certificate<UID> certificate) {
        return new CommonSession<>(certificate, cacheMessageSize);
    }

}
