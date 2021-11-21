package com.tny.game.basics.item;

import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/23 1:04 下午
 */
public class AnyEntityKeyMaker implements EntityKeyMaker<AnyId, Any> {

	private final EntityScheme scheme;

	public AnyEntityKeyMaker(EntityScheme scheme) {
		this.scheme = scheme;
	}

	@Override
	public AnyId make(Any object) {
		return makeId(object.getPlayerId(), object.getId());
	}

	@Override
	public Class<AnyId> getKeyClass() {
		return AnyId.class;
	}

	private AnyId makeId(long playerId, long id) {
		return AnyId.idOf(playerId, id);
	}

	//	public String makeId(Class<?> clazz, long playerId) {
	//		return makeKey(clazz.getSimpleName(), "#", playerId);
	//	}
	//
	//	public static String makeKey(String separator, Object... keyValues) {
	//		StringBuilder builder = new StringBuilder(45);
	//		for (Object value : keyValues) {
	//			builder.append(separator)
	//					.append(value);
	//		}
	//		return builder.toString();
	//	}

}
