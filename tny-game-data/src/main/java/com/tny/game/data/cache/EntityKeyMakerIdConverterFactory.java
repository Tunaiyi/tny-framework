package com.tny.game.data.cache;

import com.tny.game.data.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/20 5:23 下午
 */
public class EntityKeyMakerIdConverterFactory implements EntityIdConverterFactory {

	@Override
	public EntityIdConverter<?, ?, ?> createConverter(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
		return EntityKeyMakerIdConverter.wrapper(keyMaker);
	}

}
