package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 */
@UnitInterface
public interface SessionSetting extends EndpointSetting {

    int getSendMessageCachedSize();

}
