package com.tny.game.cache;

import java.io.Serializable;
import java.util.*;

/**
 * @author KGTny
 * @ClassName: LockedCache
 * @Description: cache获取对象集合实体
 * @date 2011-10-8 下午4:57:59
 * <p>
 * cache获取对象集合实体
 * <p>
 * cache通过加锁获取对象返回的集合实体,可以通过实体获取加锁的对象,并进行解锁<br>
 */
public final class LockedCache {

    /**
     * 获取加锁的对象
     */
    private Map<String, Object> entityMap = new HashMap<String, Object>();

    /**
     * 缓存对象
     */
    protected DirectCache cache;

    /**
     * 构造函数
     *
     * @param objectMap 加锁的对象Map
     * @param cache     缓存对象
     */
    protected LockedCache(Map<String, Object> objectMap, DirectCache cache) {
        this.cache = cache;
        this.entityMap.putAll(objectMap);
    }

    /**
     * 获取指定key的加锁对象
     * <p>
     * <p>
     * 获取指定key的加锁对象<br>
     *
     * @param keyCollection 指定的key
     * @param clazz         获取对象类型
     * @return 返回获取的对象
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object entity = entityMap.get(key);
        if (entity != null) {
            if (clazz.isInstance(entity))
                return (T) entity;
        }
        return null;
    }

    /**
     * 获取指定key集合的加锁对象集合
     * <p>
     * <p>
     * 获取指定key集合的加锁对象集合<br>
     *
     * @param keyCollection 指定的key集合
     * @param clazz         获取对象类型
     * @return 返回对象集合
     */
    public <T extends Serializable> Collection<T> getCollection(Collection<String> keyCollection,
                                                                Class<T> clazz) {
        List<T> entityList = new ArrayList<T>(keyCollection.size());
        for (String key : keyCollection) {
            T object = this.get(key, clazz);
            if (object != null)
                entityList.add(object);
        }
        return entityList;
    }

    /**
     * 获取指定key集合的加锁对象Map
     * <p>
     * <p>
     * 获取指定key集合的加锁对象Map<br>
     *
     * @param keyCollection 指定的key集合
     * @param clazz         获取对象类型
     * @return 返回对象Map
     */
    public <T extends Serializable> Map<String, T> getMap(Collection<String> keyCollection,
                                                          Class<T> clazz) {
        Map<String, T> entityMap = new HashMap<String, T>(keyCollection.size());
        for (String key : keyCollection) {
            T object = this.get(key, clazz);
            if (object != null)
                entityMap.put(key, object);
        }
        return entityMap;
    }

    /**
     * 解除加锁对象
     * <p>
     * <p>
     * 解除加锁对象<br>
     */
    public void release() {
//		SortedSet<String> keySet = cache.formatSortLockKeyList(this.entityMap.keySet());
//		List<String> releaseKeyList = new ArrayList<String>(keySet);
//		Collections.reverse(releaseKeyList);
//		for (String key : releaseKeyList)
//			cache.delete(key);
    }

}
