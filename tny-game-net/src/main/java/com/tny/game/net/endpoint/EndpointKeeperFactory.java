package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface EndpointKeeperFactory<UID, E extends EndpointKeeper<UID, ?>, S extends EndpointSetting> {

    E createKeeper(String userType, S setting);

}
