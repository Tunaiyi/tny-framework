package com.tny.game.net.command.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CharRange {

    public char low() default 0;

    public char high() default Character.MAX_VALUE;

}
