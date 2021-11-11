package com.tny.game.common.lifecycle.annotation;

import java.lang.annotation.*;

/**
 * Created by Kun Yang on 2016/12/15.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface StaticInit {

}
