package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DTODoc {

    String value();

    boolean push() default false;

}
