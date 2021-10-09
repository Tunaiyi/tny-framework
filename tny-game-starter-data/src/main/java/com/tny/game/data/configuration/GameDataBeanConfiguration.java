package com.tny.game.data.configuration;

import com.tny.game.data.cache.*;
import com.tny.game.data.configuration.cache.*;
import com.tny.game.data.configuration.storage.executor.*;
import com.tny.game.data.storage.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration
@EnableConfigurationProperties({
		CacheRecyclerProperties.class,
		AsyncObjectStoreExecutorProperties.class,
})
public class GameDataBeanConfiguration {

	@Bean
	@ConditionalOnClass(CacheRecyclerProperties.class)
	public ScheduledCacheRecycler scheduledCacheRecycler(CacheRecyclerProperties properties) {
		ScheduledCacheRecyclerSetting setting = properties.getScheduledCacheRecycler();
		return new ScheduledCacheRecycler(setting.getRecycleIntervalTime());
	}

	@Bean
	public AnnotationEntityKeyMakerFactory annotationEntityKeyMakerFactory() {
		return new AnnotationEntityKeyMakerFactory();
	}

	@Bean
	@ConditionalOnProperty(value = "tny.data.store-executor.fork-join.enable", havingValue = "true")
	public ForkJoinAsyncObjectStoreExecutor forkJoinAsyncObjectStoreExecutor(AsyncObjectStoreExecutorProperties properties) {
		return new SpringForkJoinAsyncObjectStoreExecutor(properties);
	}

}
