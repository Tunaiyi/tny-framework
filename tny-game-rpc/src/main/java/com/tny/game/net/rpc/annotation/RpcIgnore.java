package com.tny.game.net.rpc.annotation;

import java.lang.annotation.*;

/**
 * 忽略作为远程参数/消息体
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/11 16:01
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcIgnore {

}