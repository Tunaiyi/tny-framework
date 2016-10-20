package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IDDoc {

    String value();

    String text() default "";

}
