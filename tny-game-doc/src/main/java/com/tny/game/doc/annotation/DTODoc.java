package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DTODoc {

    public String value();

    public boolean push() default false;

}
