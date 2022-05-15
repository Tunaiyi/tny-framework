package com.tny.game.net.rpc.annotation;

import java.lang.annotation.*;

/**
 * 客户端远程服务
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/11 16:02
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcRemoteService {

    /**
     * @return 服务名
     */
    String value();

    /**
     * @return 转发服务
     */
    String forwardService() default "";

    /**
     * @return 选项
     */
    RpcRemoteOptions options() default @RpcRemoteOptions;

}