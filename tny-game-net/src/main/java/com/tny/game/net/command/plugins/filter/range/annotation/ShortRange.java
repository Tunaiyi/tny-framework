package com.tny.game.net.command.plugins.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShortRange {

    short low() default 0;

    short high() default Short.MAX_VALUE;

}