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

package com.tny.game.data.configuration.mongodb;

import com.tny.game.boot.registrar.*;
import com.tny.game.data.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Nonnull;

import static com.tny.game.boot.utils.BeanNameUtils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
public class ImportMongoStorageAccessorFactoryDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    private void registerMongoClientStorageAccessorFactory(
            BeanDefinitionRegistry registry, MongoStorageAccessorFactorySetting setting, String beanName) {
        MongoClientStorageAccessorFactory factory = new MongoClientStorageAccessorFactory(setting.getDataSource());
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(MongoClientStorageAccessorFactory.class, () -> factory);
        builder.addPropertyReference("entityIdConverterFactory", setting.getIdConverterFactory());
        builder.addPropertyReference("entityObjectConverter", setting.getEntityObjectConverter());
        if (StringUtils.isBlank(setting.getDataSource())) {
            builder.addAutowiredProperty("mongoTemplate");
        } else {
            builder.addPropertyReference("mongoTemplate", nameOf(setting.getDataSource(), MongoTemplate.class));
        }
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        MongoStorageAccessorFactoryProperties properties = loadProperties(MongoStorageAccessorFactoryProperties.class);
        if (!properties.isEnable()) {
            return;
        }
        MongoStorageAccessorFactorySetting defaultSetting = properties.getAccessor();
        if (defaultSetting != null) {
            registerMongoClientStorageAccessorFactory(registry, defaultSetting, MongoClientStorageAccessorFactory.ACCESSOR_NAME);
        }
        properties.getAccessors().forEach((name, setting) -> {
            setting.setDataSource(name);
            registerMongoClientStorageAccessorFactory(registry, setting, nameOf(name, MongoClientStorageAccessorFactory.class));
        });
    }

}
