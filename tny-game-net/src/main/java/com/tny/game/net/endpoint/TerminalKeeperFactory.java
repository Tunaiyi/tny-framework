package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface TerminalKeeperFactory<UID, S extends TerminalKeeperSetting>
        extends EndpointKeeperFactory<UID, NetEndpointKeeper<UID, Terminal<UID>>, S> {

}
