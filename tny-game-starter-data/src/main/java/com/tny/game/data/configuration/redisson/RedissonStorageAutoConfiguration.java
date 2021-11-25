package com.tny.game.data.configuration.redisson;

import com.tny.game.data.configuration.*;
import com.tny.game.data.redisson.*;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedissonStorageAccessorFactory.class)
@AutoConfigureBefore(DataAutoConfiguration.class)
@Import({
		ImportRedissonStorageAccessorFactoryDefinitionRegistrar.class
})
@EnableConfigurationProperties({
		RedissonStorageAccessorFactoryProperties.class
})
@ConditionalOnProperty(value = "tny.data.enable", matchIfMissing = true)
public class RedissonStorageAutoConfiguration {

}
