package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface SessionKeeperSetting extends EndpointKeeperSetting {

    long getOfflineCloseDelay();

    int getOfflineMaxSize();

    long getClearInterval();

    String getSessionFactory();

    SessionSetting getSession();

}
