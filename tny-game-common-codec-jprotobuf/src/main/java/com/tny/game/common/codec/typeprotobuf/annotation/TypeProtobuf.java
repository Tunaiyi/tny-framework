package com.tny.game.common.codec.typeprotobuf.annotation;

import java.lang.annotation.*;

/**
 * 注册 Json2Redis 持久化类
 * <p>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeProtobuf {

    /**
     * aa/bb 方式 mimeType 优先
     *
     * @return 等同 mimeType
     */
    int value();

}
