package com.tny.game.net.endpoint;

import com.tny.game.common.unit.*;
import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@Unit
public class CommonSessionKeeperFactory<UID> implements SessionKeeperFactory<UID, SessionKeeperSetting> {

    public CommonSessionKeeperFactory() {
    }

    @Override
    public SessionKeeper<UID> createKeeper(String userType, SessionKeeperSetting setting) {
        SessionFactory<UID, NetSession<UID>, SessionSetting> sessionFactory = UnitLoader.getLoader(SessionFactory.class)
                .getUnitAnCheck(setting.getSessionFactory());
        return new CommonSessionKeeper<>(userType, sessionFactory, setting);
    }

}
