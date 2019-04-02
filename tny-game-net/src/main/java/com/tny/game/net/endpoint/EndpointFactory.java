package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
@UnitInterface
public interface EndpointFactory<UID, E extends Endpoint<UID>, S extends EndpointSetting> {

    E create(S setting, EndpointEventHandler<UID, NetEndpoint<UID>> eventHandler);

}
