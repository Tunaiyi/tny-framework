package com.tny.game.redisson.annotation;

import com.tny.game.codec.annotation.*;

import java.lang.annotation.*;

/**
 * 注册 Json2Redis 持久化类
 * <p>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisObject {

    /**
     * @return 数据源 beam名字
     */
    String source() default "";

    /**
     * 如果配置优先查看是否由 CodecableObject 注解
     *
     * @return 序列化类型
     */
    Codable codec() default @Codable("");

}
