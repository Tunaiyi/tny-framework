package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.UnitInterface;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 22:52
 */
@UnitInterface
public interface SessionKeeperSetting extends EndpointKeeperSetting {

    String getName();

    String getKeeperFactory();

    long getOfflineCloseDelay();

    int getOfflineMaxSize();

    long getClearInterval();

}
