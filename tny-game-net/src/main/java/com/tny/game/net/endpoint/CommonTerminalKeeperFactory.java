package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@Unit
public class CommonTerminalKeeperFactory<UID> implements TerminalKeeperFactory<UID, TerminalKeeperSetting> {

    @Override
    public EndpointKeeper<UID, Terminal<UID>> createKeeper(String userType, TerminalKeeperSetting setting) {
        return new CommonTerminalKeeper<>(userType);
    }

}