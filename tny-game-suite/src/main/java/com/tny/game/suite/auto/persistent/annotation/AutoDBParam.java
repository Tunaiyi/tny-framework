package com.tny.game.suite.auto.persistent.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AutoDBParam {

    /**
     * 立即执行
     *
     * @return
     */
    boolean imm() default false;

    /**
     * 操作
     *
     * @return
     */
    String op() default AutoOP.SAVE;

}
