package com.tny.game.net.rpc.annotation;

import com.tny.game.net.rpc.*;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcCaller {

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

    /**
     * @return 发送消息方式, 请求/推送/响应 默认为请求.
     */
    RpcMode mode() default RpcMode.REQUEST;

    /**
     * @return 调用方式
     */
    RpcInvocation invocation() default RpcInvocation.DEFAULT;

    /**
     * @return 是否是寂寞方式(不抛出异常)
     */
    boolean silently() default false;

    /**
     * -1 为 setting 配置时间,
     * >= 0 超时
     *
     * @return 超时
     */
    long timeout() default -1;

    /**
     * @return 路由器类
     */
    Class<? extends RpcRouter> router() default RpcRouter.class;

}