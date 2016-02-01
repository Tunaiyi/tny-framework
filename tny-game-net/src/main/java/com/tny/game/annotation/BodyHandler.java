package com.tny.game.annotation;

import com.tny.game.net.client.nio.ResponseMode;

import java.lang.annotation.*;

/**
 * 响应消息体处理器
 *
 * @author KGTny
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BodyHandler {

    /**
     * 处理来自包含以下协议的消息体
     *
     * @return
     */
    public int[] includeProtocol() default {};

    /**
     * 不处理来自以下协议的消息体
     *
     * @return
     */
    public int[] excludeProtocol() default {};

    /**
     * 指出来改用户组的消息, 默认全部处理
     *
     * @return
     */
    public String[] userGroup() default {};

    /**
     * 处理消息体的响应类型(返回,推送),默认是全部处理
     *
     * @return
     */
    public ResponseMode responseMode() default ResponseMode.ALL;

}