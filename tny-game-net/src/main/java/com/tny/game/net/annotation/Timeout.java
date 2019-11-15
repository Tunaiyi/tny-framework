package com.tny.game.net.annotation;

import java.lang.annotation.*;

/**
 * Contoller需要认证
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Timeout {

    long value() default 60000L;

}