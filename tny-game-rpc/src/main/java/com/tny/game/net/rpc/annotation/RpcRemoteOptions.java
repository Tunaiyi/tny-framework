package com.tny.game.net.rpc.annotation;

import com.tny.game.net.rpc.*;

import java.lang.annotation.*;

/**
 * Rpc远程选项
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/1 23:57
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcRemoteOptions {

    /**
     * @return 调用方式
     */
    RpcInvocation invocation() default RpcInvocation.DEFAULT;

    /**
     * @return 是否是寂寞方式(不抛出异常)
     */
    boolean silently() default false;

    /**
     * @return 代理服务
     */
    String proxyService() default "";

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
