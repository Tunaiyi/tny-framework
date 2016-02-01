package com.tny.game.net.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShortRange {

    public short low() default 0;

    public short high() default Short.MAX_VALUE;

}
