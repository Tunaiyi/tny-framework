package com.tny.game.basics.item;

import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/23 1:04 下午
 */
public class AnyEntityKeyMaker implements EntityKeyMaker<AnyId, Any> {

	private final AnyIdConverter converter;

	public AnyEntityKeyMaker(EntityScheme scheme) {
		this.converter = new AnyIdConverter(scheme);
	}

	@Override
	public AnyId make(Any object) {
		return converter.object2AnyId(object);
	}

	@Override
	public Class<AnyId> getKeyClass() {
		return AnyId.class;
	}

}
