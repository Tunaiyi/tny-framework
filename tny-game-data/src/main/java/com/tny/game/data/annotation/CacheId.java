package com.tny.game.data.annotation;

import java.lang.annotation.*;

/**
 * <p>
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheId {

    /**
     * 此属性只在 Class 上有效
     * 构成key的方法名称数组 如: key = getId() + "_" + getName();
     * <p>
     * cacheKeys = {"id", "name"}
     * <p>
     * 或使用 ObjectId 进行标识. Id 的方法或字段
     *
     * @see CacheId
     */
    String[] idFields() default {};

    /**
     * @return 连接符
     */
    String link() default "_";

}