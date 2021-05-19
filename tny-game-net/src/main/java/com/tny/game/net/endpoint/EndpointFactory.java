package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface EndpointFactory<UID, E extends Endpoint<UID>, S extends EndpointSetting> {

    E create(S setting, EndpointContext<UID> endpointContext);

}
