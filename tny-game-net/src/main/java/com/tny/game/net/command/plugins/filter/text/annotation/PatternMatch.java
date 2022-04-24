package com.tny.game.net.command.plugins.filter.text.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PatternMatch {

    /**
     * @return 正则表达式
     */
    String value();

    /**
     * 默认错误 code
     *
     * @return 207
     */
    int illegalCode();

}
