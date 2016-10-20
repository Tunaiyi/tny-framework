package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DTODoc {

    String value();

    String text() default "";

    boolean push() default false;

}
