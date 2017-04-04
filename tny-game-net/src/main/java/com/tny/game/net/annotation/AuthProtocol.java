package com.tny.game.net.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Kun Yang on 2017/4/1.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthProtocol {

    /**
     * 用户组名称
     * <p>
     * <p>
     * 当userType System<br>
     *
     * @return
     */
    int[] value();

    /**
     * @return 是否是全部
     */
    boolean all() default false;

}