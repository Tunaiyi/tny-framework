package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClassDoc {

    /**
     * @return 描述
     */
    String value();

    /**
     * @return 类名
     */
    String name() default "";

    /**
     * @return 细节描述
     */
    String text() default "";

}
