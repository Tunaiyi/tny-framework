package com.tny.game.data.cache;

/**
 * 是否那个策略
 *
 * @author KGTny
 */
public interface ReleaseStrategyFactory<K extends Comparable<K>, O> {

    /**
     * 获取策略对象
     *
     * @param key    键值
     * @param object 对象
     * @return 返回释放策略对象
     */
    ReleaseStrategy<K, O> createStrategy(K key, O object);

}
