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

package com.tny.game.data.configuration.storage;

import com.tny.game.boot.registrar.*;
import com.tny.game.data.storage.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
public class ImportQueueObjectStorageFactoryDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {


    private void registerQueueObjectStorageFactory(BeanDefinitionRegistry registry, QueueObjectStorageFactorySetting setting, String beanName) {
        QueueObjectStorageFactory factory = new QueueObjectStorageFactory();
        registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
                .genericBeanDefinition(QueueObjectStorageFactory.class, () -> factory)
                .addPropertyReference("storeExecutor", setting.getStoreExecutor())
                .addPropertyReference("accessorFactory", setting.getAccessorFactory())
                .getBeanDefinition());
    }

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        AsyncObjectStorageFactoriesProperties properties = loadProperties(AsyncObjectStorageFactoriesProperties.class);
        if (!properties.isEnable()) {
            return;
        }
        QueueObjectStorageFactorySetting queueSetting = properties.getStorage();
        if (queueSetting != null) {
            registerQueueObjectStorageFactory(registry, queueSetting, QueueObjectStorageFactory.STORAGE_NAME);
        }
        properties.getStorages().forEach((name, setting) -> registerQueueObjectStorageFactory(registry, setting, name));
    }

}
