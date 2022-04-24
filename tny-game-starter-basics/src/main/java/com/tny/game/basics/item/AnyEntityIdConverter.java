package com.tny.game.basics.item;

import com.tny.game.data.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 2:59 下午
 */
public class AnyEntityIdConverter implements EntityIdConverter<AnyId, Any, String> {

    private final AnyIdConverter converter;

    public AnyEntityIdConverter(EntityScheme scheme) {
        converter = new AnyIdConverter(scheme);
    }

    @Override
    public String keyToId(AnyId key) {
        return converter.anyId2Key(key);
    }

    @Override
    public String entityToId(Any object) {
        return converter.any2Key(object);
    }

}
