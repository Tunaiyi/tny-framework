package com.tny.game.cache;

import java.util.*;

/**
 * @author KGTny
 * @ClassName: Cache
 * @Description: 缓存接口
 * @date 2011-9-27 下午6:12:01
 * <p>
 * 规定缓存接口
 * <p>
 * 提供一些操作缓存的方法<br>
 */
public interface Cache {

    /**
     * 删除指定的对象 对象必须带有(@ToCache注解) 并且会删除带有@Link注解肿 delete = true关联项对应的对象
     *
     * @param object 指定的对象
     * @return 删除成功返回true 失败返回false
     */
    public boolean deleteObject(Object object);

    /**
     * 删除指定的对象列表 对象必须带有(@ToCache注解) 并且会删除带有@Link注解肿 delete = true关联项对应的对象
     *
     * @param objects 指定的对象列表
     * @return 返回删除失败的列表
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> deleteObject(T... objects);

    /**
     * 删除指定的对象集合 对象必须带有(@ToCache注解) 并且会删除带有@Link注解肿 delete = true关联项对应的对象
     *
     * @param keys 指定的对象集合
     * @return 返回删除失败的列表
     */
    public <T> List<T> deleteObject(Collection<T> objectCollection);

    /**
     * 通过指定Class和构成key相关数值获取对应的对象
     * Class必须带有(@ToCache注解),此方法回更具ToCache的配置和keyValue的值构建key
     * 并且查找出带有@Link注解的字段的关联项，赋值给关联项 最后返回该对象
     *
     * @param clazz    获取指定Class必须带有(@ToCache注解)
     * @param keyValue 获取对象相关的值
     * @return 返回获取的对象，没有则返回false 类型不对则throws ClassCastException
     */
    public <T> T getObject(Class<T> clazz, Object... keyValues);

    /**
     * 获取指定key对象 通过指定Class和构成key相关数值获取对应的对象
     * Class必须带有(@ToCache注解),此方法回更具ToCache的配置和keyValue的值构建key
     * 并且查找出带有@Link注解的字段的关联项，赋值给关联项 最后返回该对象
     * <p>
     * 获取指定key集合的对象<br>
     *
     * @param keys  指定key Map
     * @param clazz 集合类型
     * @return 返回获取的指定key集合对象 Map
     */
    public <T> T getObjectByKey(Class<T> clazz, String key);

    /**
     * 获取指定key对象
     * <p>
     * <p>
     * 获取指定key集合的对象<br>
     *
     * @param keys  指定key Map
     * @param clazz 集合类型
     * @return 返回获取的指定key集合对象 Map
     */
    public <T> Collection<? extends T> getObjectsByKeys(Class<T> clazz, Collection<String> keys);

    /**
     * 获取指定key对象
     * <p>
     * <p>
     * 获取指定key集合的对象<br>
     *
     * @param keys  指定key Map
     * @param clazz 集合类型
     * @return 返回获取的指定key集合对象 Map
     */
    public <T> Map<String, ? extends T> getObjectMapByKeys(Class<T> clazz, Collection<String> keys);

    /**
     * 增加键值
     * <p>
     * <p>
     * 增加键值,若有缓存中已存在指定的key则增加失败,若无则添加成功<br>
     *
     * @param value 指定的值
     * @return 添加成功返回true 失败返回false
     */
    public boolean addObject(Object value);

    /**
     * 增加键值
     * <p>
     * <p>
     * 增加键值,若有缓存中已存在指定的key则增加失败,若无则添加成功<br>
     *
     * @param value 指定的值
     * @return 添加成功返回true 失败返回false
     */
    public boolean addObject(Object value, long millisecond);

    /**
     * 批量增加键值
     * <p>
     * <p>
     * 批量增加键值,若有缓存中已存在指定的key则增加失败,若无则添加成功<br>
     *
     * @param value 指定的值
     * @return 添加成功返回true 失败返回false
     */
    public <T> List<T> addObject(Collection<T> objectCollection);

    /**
     * 批量增加键值
     * <p>
     * <p>
     * 批量增加键值,若有缓存中已存在指定的key则增加失败,若无则添加成功<br>
     *
     * @param value       指定的值
     * @param millisecond 过期时间
     * @return 添加成功返回true 失败返回false
     */
    public <T> List<T> addObject(Collection<T> objectCollection, long millisecond);

    /**
     * 设置键值
     * <p>
     * <p>
     * 设置键值,若有缓存中已存在指定的key则覆盖其value,若无则添加成功<br>
     *
     * @param key   指定的键
     * @param value 指定的值
     * @return 设置成功返回true 失败返回false
     */
    public boolean setObject(Object value);

    /**
     * 设置键值
     * <p>
     * <p>
     * 设置键值,若有缓存中已存在指定的key则覆盖其value,若无则添加成功<br>
     *
     * @param key         指定的键
     * @param value       指定的值
     * @param millisecond 过期时间
     * @return 设置成功返回true 失败返回false
     */
    public boolean setObject(Object value, long millisecond);

    /**
     * 批量设置键值
     * <p>
     * <p>
     * 批量设置键值,若有缓存中已存在指定的key则覆盖其value,若无则添加成功<br>
     *
     * @param key   指定的键
     * @param value 指定的值
     * @return 设置成功返回true 失败返回false
     */
    public <T> List<T> setObject(Collection<T> objectCollection);

    /**
     * 批量设置键值
     * <p>
     * <p>
     * 批量设置键值,若有缓存中已存在指定的key则覆盖其value,若无则添加成功<br>
     *
     * @param key              指定的键
     * @param objectCollection 指定的值几盒
     * @param millisecond      有效时间
     * @return 设置成功返回true 失败返回false
     */
    public <T> List<T> setObject(Collection<T> objectCollection, long millisecond);

    /**
     * 替换键值
     * <p>
     * <p>
     * 替换键值,若有缓存中已存在指定的key则覆盖其value,若无则不操作<br>
     *
     * @param key   指定的键
     * @param value 指定的值
     * @return 替换返回true 失败返回false
     */
    public boolean updateObject(Object value);

    /**
     * 替换键值
     * <p>
     * <p>
     * 替换键值,若有缓存中已存在指定的key则覆盖其value,若无则不操作<br>
     *
     * @param key         指定的键
     * @param value       指定的值
     * @param millisecond 过期时间
     * @return 替换返回true 失败返回false
     */
    public boolean updateObject(Object value, long millisecond);

    /**
     * 批量替换键值
     * <p>
     * <p>
     * 批量替换键值,若有缓存中已存在指定的key则覆盖其value,若无则添加成功<br>
     *
     * @param key   指定的键
     * @param value 指定的值
     * @return 设置成功返回true 失败返回false
     */
    public <T> List<T> updateObject(Collection<T> objectCollection);

    /**
     * 批量替换键值
     * <p>
     * <p>
     * 批量替换键值,若有缓存中已存在指定的key则覆盖其value,若无则添加成功<br>
     *
     * @param key              指定的键
     * @param objectCollection 指定的值几盒
     * @param millisecond      有效时间
     * @return 设置成功返回true 失败返回false
     */
    public <T> List<T> updateObject(Collection<T> objectCollection, long millisecond);

    /**
     * 获取指定key带有版本号的Cas对象
     * <p>
     * <p>
     * 获取指定key带有版本号的Cas对象<br>
     *
     * @param key   指定key
     * @param clazz 对象类型
     * @return 返回带有版本号的Cas对象
     */
    public <T> CasItem<T> getsObject(Class<T> clazz, String key);

    /**
     * 获取指定key带有版本号的Cas对象
     * <p>
     * <p>
     * 获取指定key带有版本号的Cas对象<br>
     *
     * @param key   指定key
     * @param clazz 对象类型
     * @return 返回带有版本号的Cas对象
     */
    public <T> CasItem<T> getsObject(Class<T> clazz, Object... keyValues);

    /**
     * 通过cas的方式更新item的value
     * <p>
     * <p>
     * 通过cas的方式更新item的value,若版本未被更新则更新成功,若版本被覆盖则返回false<br>
     *
     * @param item 更新的项
     * @return 更新返回true 失败返回false
     */
    public <T> boolean casObject(CasItem<T> item);

    /**
     * 通过cas的方式更新item的value
     * <p>
     * <p>
     * 通过cas的方式更新item的value,若版本未被更新则更新成功,若版本被覆盖则返回false<br>
     *
     * @param item        更新的项
     * @param millisecond 有效期 毫秒
     * @return 更新返回true 失败返回false
     */
    public <T> boolean casObject(CasItem<T> item, long millisecond);

    /**
     * 通过对象获取Key
     *
     * @param key
     * @return
     */
    public String getKey(Object object);

    /**
     * 通过对象类型与值获取key
     *
     * @param clazz
     * @param keyValues
     * @return
     */
    public String getKey(Class<?> clazz, Object... keyValues);

    public void shutdown() throws InterruptedException;

}
