package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface EndpointKeeperFactory<UID, E extends EndpointKeeper<UID, ?>, S extends EndpointKeeperSetting> {

	E createKeeper(String userType, S setting);

}
