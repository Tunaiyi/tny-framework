package com.tny.game.redisson.annotation;

import java.lang.annotation.*;

/**
 * 用于注册第三放数据结构无法加 Json2Redis
 * 可通过配置 Json2RedisRegistrar 进行注册
 * <p>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisObjectRegistrar {

    /**
     * @return 持久化类
     */
    Class<?> value();

    /**
     * @return 格式化
     */
    RedisObject object();

}
