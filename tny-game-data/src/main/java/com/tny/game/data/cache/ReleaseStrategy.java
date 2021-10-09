package com.tny.game.data.cache;

/**
 * 是否那个策略
 *
 * @author KGTny
 */
public interface ReleaseStrategy<K extends Comparable<?>, O> {

	/**
	 * 是否释放该对象
	 *
	 * @param entity 释放的对象
	 * @return 释放则返回true， 不是放返回 false
	 */
	boolean release(CacheEntry<K, O> entity, long releaseAt);

	/**
	 * 访问
	 */
	void visit();

}
