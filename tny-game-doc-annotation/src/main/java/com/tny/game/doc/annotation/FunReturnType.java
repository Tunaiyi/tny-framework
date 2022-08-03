package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FunReturnType {

    /**
     * @return 返回类型
     */
    Class<?> value();

}
