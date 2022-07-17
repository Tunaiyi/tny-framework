package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface VarDoc {

    /**
     * @return 注释
     */
    String value();

}
