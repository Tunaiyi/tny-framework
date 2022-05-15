package com.tny.game.net.rpc.annotation;

import java.lang.annotation.*;

/**
 * 可路由参数
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/11 16:02
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcRouteParam {

}