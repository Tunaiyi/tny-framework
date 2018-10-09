package com.tny.game.net.command.plugins.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FloatRange {

    float low() default 0;

    float high() default Float.MAX_VALUE;

}
