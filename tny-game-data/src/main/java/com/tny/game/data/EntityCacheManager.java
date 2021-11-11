package com.tny.game.data;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.exception.*;
import com.tny.game.data.storage.*;
import org.slf4j.*;

import java.util.concurrent.locks.Lock;

/**
 * <p>
 */
public class EntityCacheManager<K extends Comparable<?>, O> implements EntityManager<K, O> {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityCacheManager.class);

	/**
	 * 缓存
	 */
	private ObjectCache<K, O> cache;

	/**
	 * id 构建器
	 */
	private EntityKeyMaker<K, O> keyMaker;

	/**
	 * 持久化
	 */
	private ObjectStorage<K, O> storage;

	/**
	 * 对象锁管理器
	 */
	private ObjectLocker<K> locker;

	public EntityCacheManager() {
	}

	public EntityCacheManager(EntityKeyMaker<K, O> keyMaker, ObjectCache<K, O> cache, ObjectStorage<K, O> storage, int currentLevel) {
		this(keyMaker, cache, storage, new HashObjectLocker<>(currentLevel));
	}

	public EntityCacheManager(EntityKeyMaker<K, O> keyMaker, ObjectCache<K, O> cache, ObjectStorage<K, O> storage, ObjectLocker<K> locker) {
		this.keyMaker = keyMaker;
		this.cache = cache;
		this.storage = storage;
		this.locker = locker;
	}

	private K idOf(O object) {
		return this.keyMaker.make(object);
	}

	@Override
	public O loadEntity(K id, EntityCreator<K, O> creator) {
		Lock lock = this.locker.lock(id);
		O object;
		try {
			object = this.cache.get(id);
			if (object != null) {
				return object;
			}
			object = this.storage.get(id);
			if (object == null) {
				object = creator.create(id);
			}
			this.storage.insert(id, object);
			this.cache.put(id, object);
			return object;
		} catch (Throwable e) {
			LOGGER.error("", e);
			throw new EntityCacheException("load exception", e);
		} finally {
			this.locker.unlock(id, lock);
		}

	}

	@Override
	public O getEntity(K id) {
		Lock lock = this.locker.lock(id);
		O object;
		try {
			object = this.cache.get(id);
			if (object != null) {
				return object;
			}
			object = this.storage.get(id);
			if (object == null) {
				return null;
			}
			this.cache.put(id, object);
		} catch (Throwable e) {
			LOGGER.error("", e);
			throw new EntityCacheException("load exception", e);
		} finally {
			this.locker.unlock(id, lock);
		}
		return object;
	}

	@Override
	public boolean insertEntity(O object) {
		K id = idOf(object);
		Lock lock = this.locker.lock(id);
		try {
			O old = this.cache.get(id);
			if (old != null) {
				return false;
			}
			if (!this.storage.insert(id, object)) {
				return false;
			}
			this.cache.put(id, object);
		} catch (Throwable e) {
			LOGGER.error("", e);
		} finally {
			this.locker.unlock(id, lock);
		}
		return true;
	}

	@Override
	public boolean updateEntity(O object) {
		K id = idOf(object);
		Lock lock = this.locker.lock(id);
		try {
			O old = this.cache.get(id);
			if (old == null || old != object) {
				return false;
			}
			if (!this.storage.update(id, object)) {
				return false;
			}
			this.cache.put(id, object);
		} catch (Throwable e) {
			LOGGER.error("", e);
		} finally {
			this.locker.unlock(id, lock);
		}
		return true;
	}

	@Override
	public boolean saveEntity(O object) {
		K id = idOf(object);
		Lock lock = this.locker.lock(id);
		try {
			if (!this.storage.save(id, object)) {
				return false;
			}
			this.cache.put(id, object);
		} catch (Throwable e) {
			LOGGER.error("", e);
		} finally {
			this.locker.unlock(id, lock);
		}
		return true;
	}

	@Override
	public boolean deleteEntity(O object) {
		K id = idOf(object);
		Lock lock = this.locker.lock(id);
		try {
			if (this.cache.remove(id, object)) {
				this.storage.delete(id, object);
			}
		} catch (Throwable e) {
			LOGGER.error("", e);
		} finally {
			this.locker.unlock(id, lock);
		}
		return true;
	}

	protected EntityCacheManager<K, O> setCache(ObjectCache<K, O> cache) {
		this.cache = cache;
		return this;
	}

	protected EntityCacheManager<K, O> setStorage(ObjectStorage<K, O> storage) {
		this.storage = storage;
		return this;
	}

	protected EntityCacheManager<K, O> setKeyMaker(EntityKeyMaker<K, O> keyMaker) {
		this.keyMaker = keyMaker;
		return this;
	}

	protected EntityCacheManager<K, O> setCurrentLevel(int currentLevel) {
		this.locker = new HashObjectLocker<>(currentLevel);
		return this;
	}

}
