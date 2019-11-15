package com.tny.game.data.annotation;

import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;

import java.lang.annotation.*;

/**
 * Cache配置注解
 * 每个加了此注解的类都会创建一个新的 {@link ObjectCache} 对象
 * class ACacheObject 加了注解@{@link CacheObject}
 * <p>
 * class BCacheObject 加了注解@{@link CacheObject}
 * <p>
 * ACacheObject 与 BCacheObject 分别存放在2个不通 {@link ObjectCache} 对象
 * <p>
 * class CCacheObject extend ACacheObject 不加注解@{@link CacheObject}
 * 则 CCacheObject 与 ACacheObject 共用同个 {@link ObjectCache}
 * <p>
 * class DCacheObject extend ACacheObject 加注解@{@link CacheObject}
 * ACacheObject 与 DCacheObject 分别存放在2个不通 {@link ObjectCache} 对象
 * <p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheObject {

    /**
     * 则通过 cacheClass 查找存储器
     *
     * @return 缓存器类(默认是注解标识的类)
     */
    Class<?> cacheClass() default Self.class;

    /**
     * @return 是否开启
     */
    boolean enable() default true;

    /**
     * @return 对象是否可以替换, 默认为 false
     */
    boolean replaceable() default false;

    /**
     * @return 最大数量
     */
    long maxCacheSize() default -1;

    /**
     * @return 并发等级
     */
    int concurrencyLevel() default 32;

    /**
     * @return 缓存
     */
    Class<? extends ObjectCacheFactory> cache() default ObjectCacheFactory.class;

    /**
     * @return 存储器
     */
    Class<? extends ObjectStorageFactory> storage() default ObjectStorageFactory.class;

    /**
     * @return 对象释放策略
     */
    Class<? extends ReleaseStrategyFactory> releaseStrategy() default ReleaseStrategyFactory.class;

}
