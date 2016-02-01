package com.tny.game.common.config;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author KGTny
 * @ClassName: Config
 * @Description: 配置文件
 * @date 2011-10-19 下午4:52:09
 * <p>
 * 配置文件类
 * <p>
 * <br>
 */
public interface Config {

    /**
     * 创建以key为父key的子Config
     *
     * @param key       创建的key
     * @param delimiter 分隔符
     * @return 返回子Config
     */
    Config child(String key, String delimiter);

    /**
     * 创建以key为父key的子Config 默认分隔符为(.)
     *
     * @param key 创建的key
     * @return 返回子Config
     */
    Config child(String key);

    /**
     * @return 获取父Config
     */
    Optional<Config> getParent();

    /**
     * @return 获取父路径
     */
    String parentKey();

    /**
     * @return 获取父路径
     */
    String parentHeadKey();

    /**
     * 获取指定key的字符串值 <br>
     *
     * @param key 指定key
     * @return 字符串值, 无则返回null
     */
    String getStr(String key);

    /**
     * 获取指定key的字符串值, 无则返回defValue <br>
     * <br>
     *
     * @param key      指定key
     * @param defValue 指定默认值
     * @return 字符串值, 无则返回defValue
     */
    String getStr(String key, String defValue);

    /**
     * 获取指定key的整型值 <br>
     *
     * @param key 指定key
     * @return 整型值, 无则返回 0
     */
    int getInt(String key);

    /**
     * 获取指定key的整型值, 无则返回defValue <br>
     * <br>
     *
     * @param key      指定key
     * @param defValue 指定默认值
     * @return 整型值, 无则返回defValue
     */
    int getInt(String key, int defValue);

    /**
     * 获取指定key的长整型值 <br>
     *
     * @param key 指定key
     * @return 长整型值, 无则返回0L
     */
    long getLong(String key);

    /**
     * 获取指定key的长整型值, 无则返回defValue <br>
     * <br>
     *
     * @param key      指定key
     * @param defValue 指定默认值
     * @return 长整型值, 无则返回defValue
     */
    long getLong(String key, long defValue);

    /**
     * 获取指定key的双精度浮点型值 <br>
     *
     * @param key 指定key
     * @return 双精度浮点型值, 无则返回0.0
     */
    double getDouble(String key);

    /**
     * 获取指定key的双精度浮点型值, 无则返回defValue <br>
     * <br>
     *
     * @param key      指定key
     * @param defValue 指定默认值
     * @return 双精度浮点型值, 无则返回defValue
     */
    double getDouble(String key, double defValue);

    /**
     * 获取指定key的浮点型值 <br>
     *
     * @param key 指定key
     * @return 浮点型值, 无则返回0.0F
     */
    float getFloat(String key);

    /**
     * 获取指定key的浮点型值, 无则返回defValue <br>
     * <br>
     *
     * @param key      指定key
     * @param defValue 指定默认值
     * @return 浮点型值, 无则返回defValue
     */
    float getFloat(String key, float defValue);

    /**
     * 获取指定key的布尔型值 <br>
     *
     * @param key 指定key
     * @return 布尔型值, 无则返回false
     */
    boolean getBoolean(String key);

    /**
     * 获取指定key的布尔型值, 无则返回defValue <br>
     * <br>
     *
     * @param key      指定key
     * @param defValue 指定默认值
     * @return 布尔型值, 无则返回defValue
     */
    boolean getBoolean(String key, boolean defValue);

    /**
     * 获取指定key的字节型值 <br>
     *
     * @param key 指定key
     * @return 字节型值, 无则返回0
     */
    byte getByte(String key);

    /**
     * 获取指定key的字节型值, 无则返回defValue <br>
     * <br>
     *
     * @param key      指定key
     * @param defValue 指定默认值
     * @return 字节型值, 无则返回defValue
     */
    byte getByte(String key, byte defValue);

    /**
     * 通过key获取对象
     *
     * @param key
     * @return
     */
    <O> O getObject(String key);

    /**
     * 通过key获取对象
     *
     * @param key
     * @param defValue
     * @return
     */
    <O> O getObject(String key, O defValue);

    /**
     * 通过Key获取枚举
     *
     * @param key
     * @param defValue
     */
    <E extends Enum<E>> E getEnum(String key, E defValue);

    /**
     * 通过Key获取枚举
     *
     * @param key
     * @param defValue
     */
    <E extends Enum<E>> E getEnum(String key, Class<E> enumClass);

    /**
     * 获取所有的键值对
     *
     * @return
     */
    Set<Entry<String, Object>> entrySet();

    /**
     * 获取所有的key
     *
     * @return
     */
    Set<String> keySet();

    /**
     * 获取所有的value
     *
     * @return
     */
    Collection<Object> values();

    /**
     * 添加可重读接口 <br>
     *
     * @param reloadable 添加的可重读接口
     */
    void addConfigReload(ConfigReload reload);

    /**
     * 移除可重读接口 <br>
     *
     * @param reloadable 移除的可重读接口
     */
    void removeConfigReload(ConfigReload reload);

    /**
     * 清楚所有可重读接口<br>
     */
    void clearConfigReload();

    /**
     * 正则表达式
     *
     * @param regular
     * @return
     */
    <T> Map<String, T> find(String regular);

    /**
     * 正则表达式
     *
     * @param regular
     * @return
     */
    <T> Map<String, T> find(Pattern regular);
}
