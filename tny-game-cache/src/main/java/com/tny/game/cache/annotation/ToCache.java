package com.tny.game.cache.annotation;

import com.tny.game.cache.CacheTrigger;

import java.lang.annotation.*;

/**
 * 调用Cache中 **Object(...) 方法的对象必须有该注解 Cache会根据该注解构建key，用于存储删除对象
 *
 * @author KGTny
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ToCache {

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
     * 关联对象的处理器类型
     *
     * @return 处理器类型
     */
    Class<? extends CacheTrigger<?, ?, ?>>[] triggers() default {};

    /**
     * 构成key的方法名称数组 如: key = getID() + "_" + getName();
     * <p>
     * cacheKeys = {"ID", "name"}
     *
     * @return
     */
    String[] cacheKeys();

    /**
     * 数据源
     *
     * @return
     */
    String source() default "";
}
