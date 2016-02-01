package com.tny.game.net.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FloatRange {

    public float low() default 0;

    public float high() default Float.MAX_VALUE;

}
