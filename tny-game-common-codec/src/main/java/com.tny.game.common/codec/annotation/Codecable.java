package com.tny.game.common.codec.annotation;

import java.lang.annotation.*;

/**
 * 注册 Json2Redis 持久化类
 * <p>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Codecable {

    /**
     * aa/bb 方式 mimeType 优先
     *
     * @return 等同 mimeType
     */
    String value();

    /**
     * aa/bb 方式
     *
     * @return 格式化
     */
    String mimeType() default "";

}
