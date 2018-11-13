package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.UnitInterface;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 11:54
 */
@UnitInterface
public interface EndpointKeeperFactory<UID, E extends Endpoint<UID>, S extends EndpointKeeperSetting> {

    EndpointKeeper<UID, E> createKeeper(String userType, S setting);

}
