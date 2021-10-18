package com.tny.game.data.cache;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/26 11:53 上午
 */
public interface ObjectCacheFactory {

	<K extends Comparable<?>, O> ObjectCache<K, O> createCache(EntityScheme cacheScheme, EntityKeyMaker<K, O> keyMaker);

}
