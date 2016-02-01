package com.tny.game.suite.net.filter.annotation;

import java.lang.annotation.*;

/**
 * 名字限制 过滤字
 *
 * @author KunYang
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NameFilter {

    int lowLength() default 2;

    int highLength() default 5;

}
