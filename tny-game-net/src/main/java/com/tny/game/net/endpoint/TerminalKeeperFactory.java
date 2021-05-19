package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface TerminalKeeperFactory<UID, S extends TerminalKeeperSetting>
        extends EndpointKeeperFactory<UID, EndpointKeeper<UID, Terminal<UID>>, S> {

}
