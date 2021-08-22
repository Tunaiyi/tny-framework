package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;

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
				.checkUnit(setting.getSessionFactory());
		return new CommonSessionKeeper<>(userType, sessionFactory, setting);
	}

}
