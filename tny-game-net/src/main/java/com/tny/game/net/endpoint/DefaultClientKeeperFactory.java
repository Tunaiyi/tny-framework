package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.Unit;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-01 19:34
 */
@Unit
public class DefaultClientKeeperFactory<UID> implements ClientKeeperFactory<UID> {

    @Override
    public EndpointKeeper<UID, Client<UID>> createKeeper(String userType, EndpointKeeperSetting setting) {
        return new DefalutClientKeeper<>(userType);
    }

}
