package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

/**
 * <p>
 */
@Unit
public class CommonTerminalKeeperFactory<UID> implements TerminalKeeperFactory<UID, TerminalKeeperSetting> {

    @Override
    public NetEndpointKeeper<UID, Terminal<UID>> createKeeper(MessagerType messagerType, TerminalKeeperSetting setting) {
        return new CommonTerminalKeeper<>(messagerType);
    }

}