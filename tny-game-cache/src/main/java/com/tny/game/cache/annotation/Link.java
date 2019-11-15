package com.tny.game.cache.annotation;

import com.tny.game.asyndb.*;

import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Deprecated
public @interface Link {

    /**
     * 重新定义项的名称，默认为字段名
     *
     * @return
     */
    public String name() default "";

    /**
     * 是否操作拥有者的时候忽略更新所属项
     *
     * @return
     */
    public boolean ignore() default false;

    /**
     * 当ignore为true并且拥有者操作以下操作的时候忽略更新所属项
     *
     * @return
     */
    public Operation[] ignoreOperation() default {Operation.INSERT, Operation.DELETE, Operation.SAVE, Operation.UPDATE};

}
