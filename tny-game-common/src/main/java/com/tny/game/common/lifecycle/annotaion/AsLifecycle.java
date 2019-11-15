package com.tny.game.common.lifecycle.annotaion;

import java.lang.annotation.*;

/**
 * Created by Kun Yang on 2016/12/15.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AsLifecycle {

    /**
     * order 越大越先执行
     *
     * @return
     */
    int order() default 0;

}
