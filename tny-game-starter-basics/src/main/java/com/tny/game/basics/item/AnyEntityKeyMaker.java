package com.tny.game.basics.item;

import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/23 1:04 下午
 */
public class AnyEntityKeyMaker implements EntityKeyMaker<String, Any> {

	private final EntityScheme scheme;

	public AnyEntityKeyMaker(EntityScheme scheme) {
		this.scheme = scheme;
	}

	@Override
	public String make(Any object) {
		return makeId(object.getClass(), object.getPlayerId(), object.getId());
	}

	@Override
	public Class<String> getKeyClass() {
		return String.class;
	}

	public String makeId(Class<?> clazz, long playerId, Object id) {
		return getKey(clazz.getSimpleName(), "#", playerId, id);
	}

	public String makeId(Class<?> clazz, long playerId) {
		return getKey(clazz.getSimpleName(), "#", playerId);
	}

	public static String getKey(String separator, Object... keyValues) {
		StringBuilder builder = new StringBuilder(45);
		for (Object value : keyValues) {
			builder.append(separator)
					.append(value);
		}
		return builder.toString();
	}

}
