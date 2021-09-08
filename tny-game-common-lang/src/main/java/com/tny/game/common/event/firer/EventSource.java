package com.tny.game.common.event.firer;

import com.tny.game.common.event.bus.*;

import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 3:28 下午
 */
public interface EventSource<L> extends EventBus<L> {

	void addListener(Collection<? extends L> listeners);

	void removeListener(Collection<? extends L> listeners);

}
