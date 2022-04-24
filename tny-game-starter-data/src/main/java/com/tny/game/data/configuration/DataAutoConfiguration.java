package com.tny.game.data.configuration;

import com.tny.game.data.cache.*;
import com.tny.game.data.configuration.cache.*;
import com.tny.game.data.configuration.manager.*;
import com.tny.game.data.configuration.storage.*;
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
@Configuration(proxyBeanMethods = false)
@Import({
        ImportReleaseStrategyDefinitionRegistrar.class,
        ImportLocalObjectCacheFactoryDefinitionRegistrar.class,
        ImportQueueObjectStorageFactoryDefinitionRegistrar.class,
        ImportEntityCacheManagerDefinitionRegistrar.class
})
@EnableConfigurationProperties({
        DataConfigurationProperties.class,
        CacheRecyclerProperties.class,
        ReleaseStrategyProperties.class,
        LocalObjectCacheFactoriesProperties.class,
        EntityCacheManagerProperties.class,
        AsyncObjectStorageFactoriesProperties.class,
        AsyncObjectStoreExecutorProperties.class,

})
@ConditionalOnProperty(value = "tny.data.enable", matchIfMissing = true)
public class DataAutoConfiguration {

    @Bean
    @ConditionalOnClass(CacheRecyclerProperties.class)
    public ScheduledCacheRecycler scheduledCacheRecycler(CacheRecyclerProperties properties) {
        ScheduledCacheRecyclerSetting setting = properties.getScheduled();
        return new ScheduledCacheRecycler(setting.getRecycleIntervalTime());
    }

    @Bean
    public AnnotationEntityKeyMakerFactory annotationEntityKeyMakerFactory() {
        return new AnnotationEntityKeyMakerFactory();
    }

    @Bean
    public EntityKeyMakerIdConverterFactory entityKeyMakerIdConverterFactory() {
        return new EntityKeyMakerIdConverterFactory();
    }

    @Bean
    @ConditionalOnProperty(value = "tny.data.store-executor.fork-join.enable", havingValue = "true")
    public ForkJoinAsyncObjectStoreExecutor forkJoinAsyncObjectStoreExecutor(AsyncObjectStoreExecutorProperties properties) {
        return new SpringForkJoinAsyncObjectStoreExecutor(properties);
    }

}
