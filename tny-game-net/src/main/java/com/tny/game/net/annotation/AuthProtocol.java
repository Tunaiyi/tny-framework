package com.tny.game.net.annotation;

import java.lang.annotation.*;

/**
 * Created by Kun Yang on 2017/4/1.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthProtocol {

    /**
     * @return 协议号
     */
    int[] protocol();

    /**
     * @return 是否是全部
     */
    boolean all() default false;

}