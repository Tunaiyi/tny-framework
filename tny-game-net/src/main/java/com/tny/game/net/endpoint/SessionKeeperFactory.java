package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface SessionKeeperFactory<UID, S extends SessionSetting> extends EndpointKeeperFactory<UID, EndpointKeeper<UID, Session<UID>>, S> {

}
