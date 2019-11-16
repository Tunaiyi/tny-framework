package com.tny.game.net.command.plugins.filter.text.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TextLength {

    int low() default 0;

    int high() default Integer.MAX_VALUE;

    /**
     * 默认错误 code
     *
     * @return 207
     */
    int illegalCode() default 0;

}
