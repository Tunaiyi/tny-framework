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
package com.tny.game.data.configuration.manager;

import com.tny.game.boot.registrar.*;
import com.tny.game.boot.utils.*;
import com.tny.game.common.reflect.*;
import com.tny.game.common.type.*;
import com.tny.game.data.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.configuration.*;
import com.tny.game.data.storage.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
public class ImportEntityCacheManagerDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    public static final Logger LOGGER = LoggerFactory.getLogger(ImportEntityCacheManagerDefinitionRegistrar.class);

    private final static DynamicEntityCacheManagerFactory entityCacheManagerFactory = new DynamicEntityCacheManagerFactory();


    private String beamName(String name, String defaultName) {
        if (StringUtils.isBlank(name)) {
            return defaultName;
        }
        return name;
    }

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        EntityCacheManagerProperties properties = loadProperties(EntityCacheManagerProperties.class);
        if (!properties.isEnable()) {
            return;
        }
        Set<Class<?>> registeredClasses = new HashSet<>();
        for (EntityScheme scheme : EntitySchemeLoader.getAllCacheSchemes()) {
            Class<?> objectClass = scheme.getCacheClass();
            if (!registeredClasses.add(objectClass)) {
                continue;
            }
            CacheKeyMakerFactory cacheKeyMakerFactory = beanFactory.getBean(
                    beamName(scheme.keyMakerFactory(), properties.getCacheKeyMakerFactory()), CacheKeyMakerFactory.class);
            CacheKeyMaker<?, ?> keyMaker = cacheKeyMakerFactory.createMaker(scheme);

            ObjectCacheFactory objectCacheFactory = beanFactory.getBean(
                    beamName(scheme.cacheFactory(), properties.getCacheFactory()), ObjectCacheFactory.class);
            ObjectCache<?, ?> objectCache = objectCacheFactory.createCache(scheme, as(keyMaker));

            ObjectStorageFactory objectStorageFactory = beanFactory.getBean(
                    beamName(scheme.storageFactory(), properties.getStorageFactory()), ObjectStorageFactory.class);
            ObjectStorage<?, ?> objectStorage = objectStorageFactory.createStorage(scheme, as(keyMaker));

            Class<? extends Comparable<?>> keyClass = keyMaker.getKeyClass();
            if (keyClass.isPrimitive()) {
                keyClass = as(Wrapper.getWrapper(keyClass));
            }
            EntityCacheManager<?, ?> entityCacheManager = entityCacheManagerFactory.createCache(keyClass, scheme.getEntityClass());

            //			entityCacheManager.getClass()

            LOGGER.info("Load EntityCacheManager {}", entityCacheManager.getClass());
            for (Class<?> c : ReflectAide.getComponentType(entityCacheManager.getClass(), EntityCacheManager.class)) {
                System.out.println("T " + c);
                LOGGER.info("Load EntityCacheManager {} for {}", entityCacheManager.getClass(), c);
            }

            Class<EntityCacheManager<?, ?>> managerClass = as(entityCacheManager.getClass());
            String managerBeanName = BeanNameUtils.lowerCamelName(objectClass.getSimpleName() + EntityCacheManager.class.getSimpleName());
            registry.registerBeanDefinition(managerBeanName, BeanDefinitionBuilder
                    .genericBeanDefinition(managerClass, () -> entityCacheManager)
                    .addPropertyValue("cache", objectCache)
                    .addPropertyValue("keyMaker", keyMaker)
                    .addPropertyValue("storage", objectStorage)
                    .addPropertyValue("currentLevel", scheme.concurrencyLevel())
                    .getBeanDefinition());
        }
    }

}
