package com.tny.game.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/30 2:26 下午
 */
public class AbstractCachedFactory<K, O> {

	private final Map<K, O> cached = new ConcurrentHashMap<>();

	protected <T extends O> T loadOrCreate(K key, Function<K, O> creator) {
		O object = cached.get(key);
		if (object != null) {
			return as(object);
		}
		synchronized (this) {
			object = cached.get(key);
			if (object != null) {
				return as(object);
			}
			O value = creator.apply(key);
			cached.put(key, value);
			return as(value);
		}
	}

}
