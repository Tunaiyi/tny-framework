package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DTODoc {

    /**
     * @return 注释
     */
    String value();

    /**
     * @return 详细描述
     */
    String text() default "";

    /**
     * @return 是否是推送
     */
    boolean push() default false;

}
