package com.tny.game.codec.typeprotobuf.annotation;

import com.tny.game.doc.annotation.*;

import java.lang.annotation.*;

/**
 * 注册 Json2Redis 持久化类
 * <p>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DocAnnotationClass
public @interface TypeProtobuf {

    /**
     * aa/bb 方式 mimeType 优先
     *
     * @return 等同 mimeType
     */
    int value();

}
