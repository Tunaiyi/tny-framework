/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
