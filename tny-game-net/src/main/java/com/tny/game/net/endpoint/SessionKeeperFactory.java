package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface SessionKeeperFactory<UID, S extends SessionKeeperSetting> extends EndpointKeeperFactory<UID, EndpointKeeper<UID, Session<UID>>, S> {

}
