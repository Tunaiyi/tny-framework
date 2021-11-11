package com.tny.game.data;

import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/20 5:13 下午
 */
public interface EntityIdConverterFactory {

	EntityIdConverter<?, ?, ?> createConverter(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker);

}
