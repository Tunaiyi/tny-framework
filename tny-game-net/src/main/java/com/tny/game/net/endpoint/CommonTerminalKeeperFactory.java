package com.tny.game.net.endpoint;

import com.tny.game.common.unit.annotation.*;

/**
 * <p>
 */
@Unit
public class CommonTerminalKeeperFactory<UID> implements TerminalKeeperFactory<UID, TerminalSetting> {


    @Override
    public EndpointKeeper<UID, Terminal<UID>> createKeeper(String userType, TerminalSetting setting) {
        return new CommonTerminalKeeper<>(userType);
    }

}