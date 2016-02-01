package com.tny.game.net.filter.string.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrLength {

    public int low() default 0;

    public int high() default Integer.MAX_VALUE;

}
