package com.tny.game.redisson.configuration;

import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/26 2:09 下午
 */
@Configuration
@Import({ImportRedissonBeanDefinitionRegistrar.class})
public class RedissonAutoConfiguration {

}
