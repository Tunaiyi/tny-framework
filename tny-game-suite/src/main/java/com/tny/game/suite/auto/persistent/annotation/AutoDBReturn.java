package com.tny.game.suite.auto.persistent.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AutoDBReturn {

    boolean imm() default false;

    String op() default AutoOP.SAVE;

}
