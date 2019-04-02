package com.tny.game.net.endpoint;

import com.tny.game.common.unit.*;

/**
 * <p>
 */
public class CommonSessionKeeperFactory<UID> implements SessionKeeperFactory<UID, SessionSetting> {

    public CommonSessionKeeperFactory() {
    }

    @Override
    public SessionKeeper<UID> createKeeper(String userType, SessionSetting setting) {
        SessionFactory<UID, NetSession<UID>, SessionSetting> sessionFactory = UnitLoader.getLoader(SessionFactory.class)
                .getUnitAnCheck(setting.getSessionFactory());
        return new CommonSessionKeeper<>(userType, sessionFactory, setting);
    }

}
