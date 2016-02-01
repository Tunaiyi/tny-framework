package com.tny.game.common.context;

import java.util.Collection;
import java.util.Map;

/**
 * @author KGTny
 * @ClassName: Attributes
 * @Description: 属性对象接口
 * @date 2011-9-21 ����10:48:40
 * <p>
 * 属性对象接口
 * <p>
 * <br>
 */
public interface Attributes {

    /**
     * 获取指定key和类型的属性
     * <p>
     * <p>
     * 获取指定key和类型的属性<br>
     *
     * @param key   取指定key
     * @param clazz 指定类型
     * @return 有该属性返回该属性 否则返回null
     */
    public <T> T getAttribute(AttrKey<? extends T> key);

    /**
     * 获取指定key和类型的属性
     * <p>
     * <p>
     * 获取指定key和类型的属性<br>
     *
     * @param key   取指定key
     * @param clazz 指定类型
     * @return 有该属性返回该属性 否则返回null
     */
    public <T> T getAttribute(AttrKey<? extends T> key, T defaultValue);

    /**
     * 删除指定key的属性
     * <p>
     * <p>
     * 删除指定key的属性<br>
     *
     * @param key 指定的Key
     */
    public <T> T removeAttribute(AttrKey<? extends T> key);

    /**
     * 设置key和属性如果key没有的话
     * <p>
     * <p>
     * 设置key和属性<br>
     *
     * @param key   设置的key
     * @param value 设置的属性
     */
    public <T> T setAttributeIfNoKey(AttrKey<? extends T> key, T value);

    /**
     * 设置key和属性
     * <p>
     * <p>
     * 设置key和属性<br>
     *
     * @param key   设置的key
     * @param value 设置的属性
     */
    public <T> void setAttribute(AttrKey<? extends T> key, T value);

    /**
     * 批量设置属性
     * <p>
     * <p>
     * 批量设置属性 <br>
     *
     * @param map 设置的属性Map
     */
    public void setAttribute(Map<AttrKey<?>, ?> map);

    /**
     * 设置key和属性如果key没有的话<br>
     *
     * @param entry
     */
    public <T> T setAttributeIfNoKey(AttributeEntry<T> entry);

    /**
     * 设置key和属性<br>
     *
     * @param entry
     */
    public void setAttribute(AttributeEntry<?> entry);

    /**
     * 批量设置属性
     * <p>
     * <p>
     * 批量设置属性 <br>
     *
     * @param map 设置的属性Map
     */
    public void setAttribute(Collection<AttributeEntry<?>> entries);

    /**
     * 批量设置属性
     * <p>
     * <p>
     * 批量设置属性 <br>
     *
     * @param map 设置的属性Map
     */
    public void setAttribute(AttributeEntry<?>... entries);

    /**
     * 删除指定key集合的属性
     * <p>
     * <p>
     * 删除指定key集合的属性<br>
     *
     * @param keys 指定key集合的属性
     */
    public void removeAttribute(Collection<AttrKey<?>> keys);

    /**
     * 获取所有的属性键值对
     *
     * @return
     */
    public Map<AttrKey<?>, Object> getAttributeMap();

    /**
     * 删除所有的属性键值对
     *
     * @return
     */
    public void clearAttribute();

    /**
     * 是否为空
     *
     * @return
     */
    public boolean isEmpty();

}
