package com.tny.game.common.event.firer;

import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 3:42 下午
 */
public interface EventSourceObject<L> extends EventSource<L> {

	EventSource<L> event();

	@Override
	default void add(L listener) {
		event().add(listener);
	}

	@Override
	default void remove(L listener) {
		event().remove(listener);
	}

	@Override
	default void addListener(Collection<? extends L> listeners) {
		event().addListener(listeners);
	}

	@Override
	default void removeListener(Collection<? extends L> listeners) {
		event().removeListener(listeners);
	}

	@Override
	default void clear() {
		event().clear();
	}

}
