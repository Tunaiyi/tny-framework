/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
