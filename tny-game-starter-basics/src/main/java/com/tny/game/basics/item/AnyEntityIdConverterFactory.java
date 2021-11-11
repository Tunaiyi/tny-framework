package com.tny.game.basics.item;

import com.tny.game.data.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/20 5:47 下午
 */
public class AnyEntityIdConverterFactory implements EntityIdConverterFactory {

	@Override
	public EntityIdConverter<?, ?, ?> createConverter(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
		return new AnyEntityIdConverter(scheme);
	}

}
