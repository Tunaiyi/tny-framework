package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface SessionSetting extends EndpointSetting {

    String getSessionFactory();

    int getCacheSentMessageSize();

    long getOfflineCloseDelay();

    int getOfflineMaxSize();

    long getClearInterval();

}
