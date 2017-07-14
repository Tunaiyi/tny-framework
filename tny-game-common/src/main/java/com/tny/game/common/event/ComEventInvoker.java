package com.tny.game.common.event;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface ComEventInvoker<L, E extends Event<?>> {

    void invoke(L listener, E event);

}
