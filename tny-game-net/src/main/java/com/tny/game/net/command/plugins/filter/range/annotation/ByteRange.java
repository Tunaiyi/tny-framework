package com.tny.game.net.command.plugins.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ByteRange {

    byte low() default 0;

    byte high() default Byte.MAX_VALUE;

    /**
     * 默认错误 code
     *
     * @return 207
     */
    int illegalCode() default 0;

}
