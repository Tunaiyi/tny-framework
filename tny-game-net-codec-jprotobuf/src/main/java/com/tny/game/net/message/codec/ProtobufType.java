package com.tny.game.net.message.codec;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 12:53 上午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ProtobufType {

    /**
     * @return protobuf 对象类型id 标识
     */
    int value();

}
