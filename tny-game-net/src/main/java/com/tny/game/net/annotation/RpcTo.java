package com.tny.game.net.annotation;

import java.lang.annotation.*;

/**
 * Rpc调用目标
 * <p>
 * 可以传入的参数类型:
 * 1. com.tny.game.net.message.Messager : Rpc 接收者
 * 2. com.tny.game.net.base.RpcServicer : Rpc 被调用服务
 * 3. com.tny.game.net.base.RpcServicerPoint : Rpc 被调用服务点(指定连接)
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 20:54
 * @see com.tny.game.net.message.Messager
 * @see com.tny.game.net.base.RpcServicer
 * @see com.tny.game.net.base.RpcServicerPoint
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcTo {

}
