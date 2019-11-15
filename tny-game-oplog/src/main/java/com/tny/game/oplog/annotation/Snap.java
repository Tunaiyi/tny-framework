package com.tny.game.oplog.annotation;

import com.tny.game.oplog.*;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Snap {

    Class<? extends Snapper>[] value() default {};

}
