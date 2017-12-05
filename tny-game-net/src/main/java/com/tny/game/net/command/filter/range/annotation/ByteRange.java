package com.tny.game.net.command.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ByteRange {

    public byte low() default 0;

    public byte high() default Byte.MAX_VALUE;

}
