package com.tny.game.data.cache;

/**
 * 获取
 * <p>
 */
public interface ObjectCache<K extends Comparable<K>, O> {

    /**
     * 获取获取缓存对象的缓存 Id
     *
     * @param object 对象
     * @return K 缓存 Id
     */
    K idOf(O object);

    /**
     * 通过 key 获取 对象
     *
     * @param key 键值
     * @return 返回
     */
    O get(K key);

    /**
     * 放入缓存
     *
     * @param key    键值
     * @param object 对象
     */
    void putIfA(K key, O object);

    /**
     * 放入缓存
     *
     * @param key    键值
     * @param object 对象
     */
    void put(K key, O object);

    /**
     * 移除指定 key 值
     *
     * @param key 键值
     */
    O remove(K key);

    /**
     * 数量
     *
     * @return
     */
    int size();

}
