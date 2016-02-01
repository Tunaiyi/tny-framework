package com.tny.game.cache.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Deprecated
public @interface CacheID {

    public int index() default 0;

    public String name() default "";

}
