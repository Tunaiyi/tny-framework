package com.tny.game.event;

import com.tny.game.event.Event;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface ComEventDelegate<E extends Event<?>> {

    void invoke(E event);

}
