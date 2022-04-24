package com.tny.game.data.annotation;

import com.tny.game.data.cache.*;

import java.lang.annotation.*;

/**
 * Cache配置注解
 * 每个加了此注解的类都会创建一个新的 {@link ObjectCache} 对象
 * class ACacheObject 加了注解@{@link EntityObject}
 * <p>
 * class BCacheObject 加了注解@{@link EntityObject}
 * <p>
 * ACacheObject 与 BCacheObject 分别存放在2个不通 {@link ObjectCache} 对象
 * <p>
 * class CCacheObject extend ACacheObject 不加注解@{@link EntityObject}
 * 则 CCacheObject 与 ACacheObject 共用同个 {@link ObjectCache}
 * <p>
 * class DCacheObject extend ACacheObject 加注解@{@link EntityObject}
 * ACacheObject 与 DCacheObject 分别存放在2个不通 {@link ObjectCache} 对象
 * <p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EntityObject {

    /**
     * 关联key前缀
     * <p>
     * <p>
     * 关联Item的key的前缀<br>
     *
     * @return 返回key前缀
     */
    String prefix() default "";

    /**
     * 则通过 cacheClass 查找存储器
     *
     * @return 缓存器类(默认是注解标识的类)
     */
    Class<?> cache() default Self.class;

    /**
     * @return 是否开启
     */
    boolean enable() default true;

    /**
     * @return 最大数量
     */
    long maxCacheSize() default -1;

    /**
     * @return 并发等级
     */
    int concurrencyLevel() default 32;

    /**
     * @return 删除后延迟清理
     */
    int deletedDelayClear() default 50000;

    /**
     * @return 存储器工厂
     */
    String keyMakerFactory() default "";

    /**
     * @return 缓存工厂
     */
    String cacheFactory() default "";

    /**
     * @return 存储器工厂
     */
    String storageFactory() default "";

}
