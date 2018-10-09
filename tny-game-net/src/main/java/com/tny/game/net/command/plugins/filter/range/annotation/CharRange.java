package com.tny.game.net.command.plugins.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CharRange {

    char low() default 0;

    char high() default Character.MAX_VALUE;

}
