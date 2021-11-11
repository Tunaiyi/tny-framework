package com.tny.game.basics.item;

import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/20 3:16 下午
 */
public class AnyEntityKeyMakerFactory implements EntityKeyMakerFactory {

	@Override
	public EntityKeyMaker<?, ?> createMaker(EntityScheme scheme) {
		return new AnyEntityKeyMaker(scheme);
	}

}
