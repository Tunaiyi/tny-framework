package com.tny.game.common.event.bus;

import com.tny.game.common.event.*;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface ComEventDelegate<E extends Event<?>> {

    void invoke(E event);

}
