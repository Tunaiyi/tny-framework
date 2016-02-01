package com.tny.game.net.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LongRange {

    public long low() default 0;

    public long high() default Long.MAX_VALUE;

}
