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
