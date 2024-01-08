/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
    public AnnotationCacheKeyMakerFactory annotationCacheKeyMakerFactory() {
        return new AnnotationCacheKeyMakerFactory();
    }

    @Bean
    public CacheKeyMakerIdConverterFactory cacheKeyMakerIdConverterFactory() {
        return new CacheKeyMakerIdConverterFactory();
    }

    @Bean
    @ConditionalOnProperty(value = "tny.data.store-executor.fork-join.enable", havingValue = "true")
    public ForkJoinAsyncObjectStoreExecutor forkJoinAsyncObjectStoreExecutor(AsyncObjectStoreExecutorProperties properties) {
        return new SpringForkJoinAsyncObjectStoreExecutor(properties);
    }

}
