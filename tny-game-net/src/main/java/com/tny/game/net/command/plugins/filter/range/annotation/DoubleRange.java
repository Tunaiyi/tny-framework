package com.tny.game.net.command.plugins.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DoubleRange {

    double low();

    double high();

    /**
     * 默认错误 code
     *
     * @return 207
     */
    int illegalCode() default 0;
}
