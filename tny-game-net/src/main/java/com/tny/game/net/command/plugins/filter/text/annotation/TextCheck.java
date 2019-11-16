package com.tny.game.net.command.plugins.filter.text.annotation;

import java.lang.annotation.*;

/**
 * 名字限制 过滤字
 *
 * @author KunYang
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TextCheck {

    int lowLength() default 2;

    int highLength() default 5;

    /**
     * 默认错误 code
     *
     * @return 207
     */
    int illegalCode() default 0;

    /**
     * 长度错误 code
     *
     * @return 0
     */
    int lengthIllegalCode() default 0;

    /**
     * 内容错误 code
     *
     * @return 0
     */
    int contentIllegalCode() default 0;

}
