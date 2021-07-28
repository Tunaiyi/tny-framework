package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface SessionFactory<UID, S extends NetSession<UID>, ST extends SessionSetting> extends EndpointFactory<UID, S, ST> {

}
