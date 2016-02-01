package com.tny.game.cache.annotation;

import com.tny.game.cache.CacheTrigger;

import java.lang.annotation.*;

/**
 * 缓存对象格式化
 *
 * @author KGTny
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Trigger {

    /**
     * 关联对象的处理器类型
     *
     * @return 处理器类型
     */
    public Class<? extends CacheTrigger<?, ?, ?>>[] triggers();

}
