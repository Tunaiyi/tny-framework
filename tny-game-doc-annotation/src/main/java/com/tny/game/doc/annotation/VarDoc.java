package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VarDoc {

    String value();

    String text() default "";

}
