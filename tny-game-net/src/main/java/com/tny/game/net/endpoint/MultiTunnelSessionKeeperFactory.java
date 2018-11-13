package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.Unit;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-01 19:34
 */
@Unit
public class MultiTunnelSessionKeeperFactory<UID> implements SessionKeeperFactory<UID, SessionKeeperSetting> {

    @Override
    public EndpointKeeper<UID, Session<UID>> createKeeper(String userType, SessionKeeperSetting setting) {
        return new MultiTunnelSessionKeeper<>(userType, setting);
    }

}
