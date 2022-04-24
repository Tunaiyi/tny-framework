package com.tny.game.data.mongodb;

import com.tny.game.data.*;
import com.tny.game.data.cache.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 2:59 下午
 */
public class MongoEntityIdConverterFactory implements EntityIdConverterFactory {

    private static final Map<Class<?>, MongoEntityIdConverter<?, ?>> converterMap = new ConcurrentHashMap<>();

    @Override
    public EntityIdConverter<?, ?, ?> createConverter(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
        return converterMap.computeIfAbsent(scheme.getCacheClass(), MongoEntityIdConverter::new);
    }

}
