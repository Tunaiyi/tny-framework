package com.tny.game.suite.net.filter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 名字限制 过滤字
 *
 * @author KunYang
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TextFilter {

    int lowLength() default 2;

    int highLength() default 5;

}
