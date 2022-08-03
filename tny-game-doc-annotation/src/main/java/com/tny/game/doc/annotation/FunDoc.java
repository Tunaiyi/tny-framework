package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FunDoc {

    /**
     * @return 描述
     */
    String value();

    /**
     * @return 详细描述
     */
    String text() default "";

    /**
     * @return 返回描述
     */
    String returnDesc() default "";

}
