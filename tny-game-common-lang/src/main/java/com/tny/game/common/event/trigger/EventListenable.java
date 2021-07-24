package com.tny.game.common.event.trigger;

import com.tny.game.common.event.bus.*;

import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 3:28 下午
 */
public interface EventListenable<L> extends EventBus<L> {

    void add(Collection<? extends L> listeners);

    void remove(Collection<? extends L> listeners);

}
