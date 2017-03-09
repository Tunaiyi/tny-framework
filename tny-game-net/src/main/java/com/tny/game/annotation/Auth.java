package com.tny.game.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Contoller需要认证
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Auth {

    /**
     * 用户组名称
     * <p>
     * <p>
     * 当userType System<br>
     *
     * @return
     */
    String[] value() default {};

    boolean enable() default true;

}