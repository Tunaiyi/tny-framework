package com.tny.game.basics.item;

import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/23 1:04 下午
 */
public class ItemKeyMaker implements EntityKeyMaker<String, Item<?>> {

	@Override
	public String make(EntityScheme scheme, Item<?> object) {
		return makeId(object.getClass(), object.getPlayerId(), object.getId());
	}

	@Override
	public Class<String> getKeyClass() {
		return String.class;
	}

	public String makeId(Class<?> clazz, long playerId, Object id) {
		return getKey("R2", "#", playerId, id);
	}

	public String makeId(Class<?> clazz, long playerId) {
		return getKey("R2", "#", playerId);
	}

	public static String getKey(String prefix, String separator, Object... keyValues) {
		StringBuilder builder = new StringBuilder(45);
		builder.append(prefix);
		for (Object value : keyValues) {
			builder.append(separator)
					.append(value);
		}
		return builder.toString();
	}

}
