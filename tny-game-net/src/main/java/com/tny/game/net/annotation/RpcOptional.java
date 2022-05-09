package com.tny.game.net.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/2 05:20
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcOptional {

}
