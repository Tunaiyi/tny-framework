package com.tny.game.data.configuration;

import com.tny.game.data.configuration.cache.*;
import com.tny.game.data.configuration.redisson.*;
import com.tny.game.data.configuration.storage.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration
@Import({
		GameDataBeanConfiguration.class,
		ImportReleaseStrategyDefinitionRegistrar.class,
		ImportLocalObjectCacheFactoryDefinitionRegistrar.class,
		ImportQueueObjectStorageFactoryDefinitionRegistrar.class,
		ImportRedissonStorageAccessorFactoryDefinitionRegistrar.class,
		ImportEntityCacheManagerDefinitionRegistrar.class
})
@EnableConfigurationProperties({
		LocalObjectCacheFactoriesProperties.class,
		ReleaseStrategyProperties.class,
		RedissonStorageAccessorFactoryProperties.class,
		QueueObjectStorageFactoriesProperties.class
})
public class GameDataAutoConfiguration {

}
