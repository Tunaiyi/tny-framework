package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

/**
 * <p>
 */
@Unit
public class CommonSessionKeeperFactory<UID> implements SessionKeeperFactory<UID, SessionKeeperSetting> {

    public CommonSessionKeeperFactory() {
    }

    @Override
    public NetEndpointKeeper<UID, Session<UID>> createKeeper(MessagerType messagerType, SessionKeeperSetting setting) {
        SessionFactory<UID, NetSession<UID>, SessionSetting> sessionFactory = UnitLoader.getLoader(SessionFactory.class)
                .checkUnit(setting.getSessionFactory());
        return new CommonSessionKeeper<>(messagerType, sessionFactory, setting);
    }

}
