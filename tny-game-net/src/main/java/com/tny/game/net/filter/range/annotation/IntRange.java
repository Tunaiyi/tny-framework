package com.tny.game.net.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IntRange {

    public int low() default 0;

    public int high() default Integer.MAX_VALUE;

}
