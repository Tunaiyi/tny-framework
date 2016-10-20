package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClassDoc {

    String value();

    String text() default "";

}
