package com.tny.game.data.cache;

import com.tny.game.data.*;
import com.tny.game.data.storage.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/8 6:03 下午
 */
public class DynamicEntityCacheMananger<K extends Comparable<?>, O> extends EntityCacheManager<K, O> {

	public DynamicEntityCacheMananger() {
		super();
	}

	@Override
	public EntityCacheManager<K, O> setCache(ObjectCache<K, O> cache) {
		return super.setCache(cache);
	}

	@Override
	public EntityCacheManager<K, O> setStorage(ObjectStorage<K, O> storage) {
		return super.setStorage(storage);
	}

	@Override
	public EntityCacheManager<K, O> setCurrentLevel(int currentLevel) {
		return super.setCurrentLevel(currentLevel);
	}

	@Override
	public EntityCacheManager<K, O> setKeyMaker(EntityKeyMaker<K, O> keyMaker) {
		return super.setKeyMaker(keyMaker);
	}

}
