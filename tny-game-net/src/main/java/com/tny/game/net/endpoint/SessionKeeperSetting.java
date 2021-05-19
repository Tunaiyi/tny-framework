package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface SessionKeeperSetting extends EndpointKeeperSetting {

    String getSessionFactory();

    long getOfflineCloseDelay();

    int getOfflineMaxSize();

    long getClearInterval();

    SessionSetting getSession();

}
