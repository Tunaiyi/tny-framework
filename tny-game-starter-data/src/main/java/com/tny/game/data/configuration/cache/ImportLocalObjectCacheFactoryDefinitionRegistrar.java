/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.data.configuration.cache;

import com.tny.game.boot.registrar.*;
import com.tny.game.data.cache.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
public class ImportLocalObjectCacheFactoryDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    private void registerLocalObjectCacheFactory(BeanDefinitionRegistry registry, LocalObjectCacheFactorySetting setting, String beanName) {
        LocalObjectCacheFactory factory = new LocalObjectCacheFactory();
        registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
                .genericBeanDefinition(LocalObjectCacheFactory.class, () -> factory)
                .addPropertyReference("recycler", setting.getRecycler())
                .addPropertyReference("releaseStrategyFactory", setting.getReleaseStrategyFactory())
                .getBeanDefinition());
    }

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        LocalObjectCacheFactoriesProperties properties = loadProperties(LocalObjectCacheFactoriesProperties.class);
        if (!properties.isEnable()) {
            return;
        }
        LocalObjectCacheFactorySetting cacheSetting = properties.getCache();
        if (cacheSetting != null) {
            registerLocalObjectCacheFactory(registry, cacheSetting, LocalObjectCacheFactory.CACHE_NAME);
        }
        properties.getCaches().forEach((name, setting) -> registerLocalObjectCacheFactory(registry, setting, name));
    }

}
