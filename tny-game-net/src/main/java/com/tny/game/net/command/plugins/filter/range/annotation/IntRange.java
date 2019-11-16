package com.tny.game.net.command.plugins.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IntRange {

    int low() default 0;

    int high() default Integer.MAX_VALUE;

    /**
     * 默认错误 code
     *
     * @return 207
     */
    int illegalCode() default 0;
}
