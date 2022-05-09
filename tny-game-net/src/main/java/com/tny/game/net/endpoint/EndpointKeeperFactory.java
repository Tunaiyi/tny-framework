package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

/**
 * <p>
 */
@UnitInterface
public interface EndpointKeeperFactory<UID, E extends NetEndpointKeeper<UID, ?>, S extends EndpointKeeperSetting> {

    E createKeeper(MessagerType messagerType, S setting);

}
