package com.tny.game.data.cache;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 8:40 下午
 */
public interface CacheRecycler {

	void accept(RecyclableCache cache);

}
