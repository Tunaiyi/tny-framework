package com.tny.game.basics.persistent.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Modifiable {

    /**
     * @return 是否立即持久化
     */
    boolean immediately() default false;

    /**
     * @return 默认持久化操作
     */
    Modify modify() default Modify.SAVE;

}
