package com.tny.game.data.cache;

/**
 * <p>
 */
public class NoopObjectCache<K extends Comparable<?>, O> implements ObjectCache<K, O> {

	/**
	 * 缓存方案
	 */
	private CacheScheme scheme;

	public NoopObjectCache() {
	}

	public NoopObjectCache(CacheScheme scheme) {
		this.scheme = scheme;
	}

	@Override
	public CacheScheme getScheme() {
		return scheme;
	}

	@Override
	public O get(K key) {
		return null;
	}

	@Override
	public void put(K key, O value) {
	}

	@Override
	public boolean remove(K key, O value) {
		return true;
	}

	@Override
	public int size() {
		return 0;
	}

}
