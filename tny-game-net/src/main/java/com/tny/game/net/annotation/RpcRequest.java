package com.tny.game.net.annotation;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 8:01 下午
 */

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcRequest {

    /**
     * 处理协议号
     * <p>
     * <p>
     * 被Controller标记的类的模塊ID <br>
     * 被Controller标记的方法的业务方法ID <br>
     *
     * @return 处理协议号
     */
    int value();

    /**
     * @return 线路id
     */
    int line() default 0;

}
