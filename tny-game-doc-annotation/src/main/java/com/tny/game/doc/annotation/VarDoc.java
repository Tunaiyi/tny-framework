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

    /**
     * @return 细节描述
     */
    String text() default "";

    /**
     * @return 值类型
     */
    Class<?> valueType() default Object.class;

    /**
     * @return 值例子
     */
    String valueExample() default "";

}
