package com.tny.game.net.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-23 23:26
 */
@UnitInterface
@FunctionalInterface
public interface MessageRelayStrategy {

    MessageRelayStrategy NO_RELAY_STRATEGY = (head) -> false;

    boolean isRelay(MessageHead head);

}
