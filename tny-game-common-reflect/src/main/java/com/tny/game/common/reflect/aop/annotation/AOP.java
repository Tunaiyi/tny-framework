package com.tny.game.common.reflect.aop.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AOP {

    public Privileges[] value() default {Privileges.PUBLIC};

}
