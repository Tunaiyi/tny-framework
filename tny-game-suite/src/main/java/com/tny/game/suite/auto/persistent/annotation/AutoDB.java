package com.tny.game.suite.auto.persistent.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AutoDB {

    /**
     * @return 是否持久化自己
     */
    boolean value() default true;

    /**
     * @return 是否立即持久化
     */
    boolean imm() default false;

    /**
     * @return 默认持久化操作
     */
    String op() default AutoOP.SAVE;

}
