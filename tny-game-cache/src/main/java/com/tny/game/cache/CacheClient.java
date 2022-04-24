package com.tny.game.cache;

import java.util.*;

public interface CacheClient {

    /**
     * 数据元名字
     *
     * @return
     */
    String getName();

    /**
     * 获取指定key集合的对象<br>
     *
     * @param key 指定key
     * @return 返回获取的指定key对象
     */
    Object get(String key);

    /**
     * 获取指定key集合的对象集合<br>
     *
     * @param keys 指定key集合
     * @return 返回获取的指定key集合对象集合
     */
    Collection<Object> getMultis(Collection<String> keys);

    /**
     * 获取指定key集合的对象Map<br>
     *
     * @param keys 指定key集合
     * @return 返回获取的指定key集合对象Map
     */
    Map<String, Object> getMultiMap(Collection<String> keys);

    /**
     * 获取带版本的缓存项<br>
     *
     * @param key 指定key
     * @return 返回带版本的缓存项
     */
    CasItem<?> gets(String key);

    /**
     * 增加键值,若有缓存中已存在指定的key则增加失败,若无则添加成功<br>
     *
     * @param key         指定的键
     * @param value       指定的值
     * @param millisecond 有效期 毫秒
     * @return 添加成功返回true 失败返回false
     */
    boolean add(String key, Object value, long millisecond);

    /**
     * 批量增加键值cacheItems,若有缓存中已存在指定的key则增加失败,若无则添加成功<br>
     *
     * @param cacheItems
     * @return 返回失败列表
     */
    <C extends CacheItem<?>> List<C> addMultis(Collection<C> cacheItems);

    /**
     * 批量增加键值valueMap,若有缓存中已存在指定的key则增加失败,若无则添加成功<br>
     *
     * @param valueMap
     * @param millisecond
     * @return 返回失败列表
     */
    <T> List<CacheItem<T>> addMultis(Map<String, T> valueMap, long millisecond);

    /**
     * 设置键值,若有缓存中已存在指定的key则覆盖其value,若无则添加成功<br>
     *
     * @param key         指定的键
     * @param value       指定的值
     * @param millisecond 有效期 毫秒
     * @return 设置成功返回true 失败返回false
     */
    boolean set(String key, Object value, long millisecond);

    /**
     * 批量设置键值cacheItems,若有缓存中已存在指定的key则覆盖其value,若无则添加成功<br>
     *
     * @param cacheItems
     * @return 返回失败列表
     */
    <C extends CacheItem<?>> List<C> setMultis(Collection<C> cacheItems);

    /**
     * 批量设置键值valueMap,若有缓存中已存在指定的key则覆盖其value,若无则添加成功<br>
     *
     * @param valueMap
     * @param millisecond
     * @return 返回失败列表
     */
    <T> List<CacheItem<T>> setMultis(Map<String, T> valueMap, long millisecond);

    /**
     * 替换键值
     * <p>
     * <p>
     * 替换键值,若有缓存中已存在指定的key则覆盖其value,若无则不操作<br>
     *
     * @param key         指定的键
     * @param value       指定的值
     * @param millisecond 有效期 毫秒
     * @return 替换返回true 失败返回false
     */
    boolean update(String key, Object value, long millisecond);

    /**
     * 批量替换键值
     * <p>
     * <p>
     * 批量替换键值,若有缓存中已存在指定的key则覆盖其value,若无则不操作<br>
     *
     * @param cacheItems
     * @return 返回失败列表
     */
    <C extends CacheItem<?>> List<C> updateMultis(Collection<C> cacheItems);

    /**
     * 批量替换键值
     * <p>
     * <p>
     * 批量替换键值,若有缓存中已存在指定的key则覆盖其value,若无则不操作<br>
     *
     * @param valueMap
     * @param millisecond
     * @return 返回失败列表
     */
    <T> List<CacheItem<T>> updateMultis(Map<String, T> valueMap, long millisecond);

    /**
     * 删除指定Key的对象<br>
     *
     * @param key 指定的Key
     */
    boolean delete(String key);

    /**
     * 批量删除指定keys的对象<br>
     *
     * @param keys
     * @return 返回失败列表
     */
    List<String> deleteMultis(Collection<String> keys);

    /**
     * 通过cas的方式更新item的value
     * <p>
     * <p>
     * 通过cas的方式更新item的value,若版本未被更新则更新成功,若版本被覆盖则返回false<br>
     *
     * @param key         更新的键
     * @param item        更新的项
     * @param millisecond 有效期 毫秒
     * @param version     版本号
     * @return 更新返回true 失败返回false
     */
    boolean cas(CasItem<?> item, long millisecond);

    /**
     * 关闭连接<br>
     */
    void shutdown();

}
