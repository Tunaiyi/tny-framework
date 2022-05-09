package com.tny.game.net.rpc.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcProtocol {

    /**
     * 处理协议号
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

    //    /**
    //     * @return 发送消息方式, 请求/推送/响应 默认为请求.
    //     */
    //    RpcMode mode() default RpcMode.REQUEST;

}