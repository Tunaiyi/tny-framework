package com.tny.game.common.event.trigger;

import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 3:42 下午
 */
public interface EventTriggerSource<L> extends EventListenable<L> {

    EventListenable<L> triggerSource();

    @Override
    default void add(L listener) {
        triggerSource().add(listener);
    }

    @Override
    default void remove(L listener) {
        triggerSource().remove(listener);
    }

    @Override
    default void add(Collection<? extends L> listeners) {
        triggerSource().add(listeners);
    }

    @Override
    default void remove(Collection<? extends L> listeners) {
        triggerSource().remove(listeners);
    }

    @Override
    default void clear() {
        triggerSource().clear();
    }

}
