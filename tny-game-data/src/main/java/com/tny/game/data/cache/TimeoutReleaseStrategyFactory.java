package com.tny.game.data.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 */
public class TimeoutReleaseStrategyFactory<K extends Comparable<K>, O> implements ReleaseStrategyFactory<K, O> {

    private Map<Class<?>, Long> lifeTimeMap = new ConcurrentHashMap<>();

    @Override
    public ReleaseStrategy<K, O> createStrategy(K key, O object) {
        return null;
    }

}
