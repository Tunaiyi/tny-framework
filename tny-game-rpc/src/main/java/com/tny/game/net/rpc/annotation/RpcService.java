package com.tny.game.net.rpc.annotation;

import com.tny.game.net.rpc.*;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcService {

	/**
	 * @return 服务名
	 */
	String value();

	/**
	 * @return 路由器类
	 */
	Class<? extends RpcRouter> router() default FirstRpcRouter.class;

}