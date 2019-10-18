package com.tny.game.asyndb;

import java.util.Collection;
import java.util.Map;

/**
 * 异步持久化对象池
 *
 * @author KGTny
 */
public interface ObjectPool {

    /**
     * 获取指定key的对象
     *
     * @param key 指定key
     * @return 对象存在返回该对象，不存在则返回null
     */
    <T> T get(Class<T> clazz, String key);

    /**
     * 获取指定keys的对象
     *
     * @param keys 指定keys
     * @return 对象存在返回该对象，不存在则返回空Collection
     */
    <T> Collection<? extends T> gets(Class<T> clazz, Collection<String> keys);

    /**
     * 获取指定keys的对象
     *
     * @param keys 指定keys
     * @return 对象存在返回该对象，不存在则返回空Map
     */
    <T> Map<String, ? extends T> getsMap(Class<T> clazz, Collection<String> keys);

    /**
     *
     * 相对象池存放一个key-value,如果插入的Key已存在，则不插入value并返回存在的对象， 否则则插入对象，并返该对象
     *
     * @param key
     *            健
     * @param object
     *            值
     * @return 返回存在池里的对象
     */
    //	public Object put(String key, Object object);

    /**
     * 向对象池插入key-value，并提交持久化。key存在则返回false，key不存在则插入并返回true
     *
     * @param key    指定key
     * @param object 指定value
     * @return 插入成功返回true 插入失败返回false
     */
    boolean insert(String key, Object object);

    /**
     * 更新对象池中指定key的value，并提交持久化。key存在则返回则更新并返回true，key不存在则返回false
     *
     * @param key    指定key
     * @param object 更新的value
     * @return 更新成功返回true 更新失败返回false
     */
    boolean update(String key, Object object);

    /**
     * 保存对象池中指定key的value，并提交持久化。key存在则返回则更新并返回结果，key不存在则插入并返回结果
     *
     * @param key    指定key
     * @param object 指定value
     * @return 保存成功返回true 保存失败则放回false
     */
    boolean save(String key, Object object);

    /**
     * 删除对象池中指定key的value，并提交持久化。key存在则返回则删除并返回ture，key不存在则并返回false
     *
     * @param key    指定key
     * @param object 删除的值
     * @return 删除成功返回true， 删除失败返回false
     */
    boolean delete(String key, Object object);

    /**
     * 清除数据
     *
     * @return
     */
    boolean flushAll();

    void shutdown() throws InterruptedException;

}
