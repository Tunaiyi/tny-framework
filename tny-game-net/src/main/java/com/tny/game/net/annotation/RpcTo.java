package com.tny.game.net.annotation;

import java.lang.annotation.*;

/**
 * Rpc调用目标
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 20:54
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcTo {

    boolean broadcast() default false;

}
