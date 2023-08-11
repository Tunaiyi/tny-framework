/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.mongodb.configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.tny.game.boot.registrar.*;
import com.tny.game.boot.utils.*;
import com.tny.game.common.exception.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.mongo.*;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020/6/28 2:43 上午
 */

public class ImportMongodbDataSourceBeanDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata annotationMetadata, @Nonnull BeanDefinitionRegistry registry) {
        MongodbDataSourceProperties properties = loadProperties(MongodbDataSourceProperties.class);
        if (!properties.isEnable()) {
            return;
        }
        MongoEntityClasses mongoEntityClasses = beanFactory.getBean(MongoEntityClasses.class);

        ObjectProvider<MongoCustomConversions> conversionsObjectProvider = this.beanFactory.getBeanProvider(MongoCustomConversions.class);
        MongoCustomConversions conversions = conversionsObjectProvider
                .orderedStream()
                .findFirst()
                .orElseGet(() -> new MongoCustomConversions(Collections.emptyList()));

        ObjectProvider<MongoClientSettingsBuilderCustomizer> customizerObjectProvider = this.beanFactory.getBeanProvider(
                MongoClientSettingsBuilderCustomizer.class);
        List<MongoClientSettingsBuilderCustomizer> customizers = customizerObjectProvider
                .orderedStream()
                .collect(Collectors.toList());

        MongodbDataSourceSetting sourcesSetting = properties.getSetting();
        if (sourcesSetting != null) {
            registerMongoBeanFactory(registry, sourcesSetting, mongoEntityClasses, customizers, conversions, "default", true);
        }
        properties.getSettings()
                .forEach((name, setting) -> registerMongoBeanFactory(registry, setting, mongoEntityClasses, customizers, conversions, name, false));
    }

    private void registerMongoBeanFactory(BeanDefinitionRegistry registry, MongodbDataSourceSetting properties,
            MongoEntityClasses mongoEntityClasses, List<MongoClientSettingsBuilderCustomizer> customizers,
            MongoCustomConversions conversions, String beanName, boolean primary) {
        try {
            MongoClientSettings clientSettings = MongoClientSettings.builder().build();

            MongoPropertiesClientSettingsBuilderCustomizer customizer =
                    new MongoPropertiesClientSettingsBuilderCustomizer(properties);
            List<MongoClientSettingsBuilderCustomizer> builderCustomizers = new ArrayList<>();
            builderCustomizers.add(customizer);
            builderCustomizers.addAll(customizers);

            MongoClient mongoClient = new MongoClientFactory(builderCustomizers)
                    .createMongoClient(clientSettings);
            String clientBeanName = BeanNameUtils.nameOf(beanName, MongoClient.class);
            registry.registerBeanDefinition(clientBeanName, BeanDefinitionBuilder
                    .genericBeanDefinition(MongoClient.class, () -> mongoClient)
                    .setPrimary(primary)
                    .getBeanDefinition());

            MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(mongoClient, properties.getMongoClientDatabase());
            String factoryBeanName = BeanNameUtils.nameOf(beanName, MongoDatabaseFactory.class);
            registry.registerBeanDefinition(factoryBeanName, BeanDefinitionBuilder
                    .genericBeanDefinition(MongoDatabaseFactory.class, () -> factory)
                    .setPrimary(primary)
                    .getBeanDefinition());

            MongoMappingContext context = mongoMappingContext(mongoEntityClasses, properties, conversions);
            String contextBeanName = BeanNameUtils.nameOf(beanName, MongoMappingContext.class);
            registry.registerBeanDefinition(contextBeanName, BeanDefinitionBuilder
                    .genericBeanDefinition(MongoMappingContext.class, () -> context)
                    .setPrimary(primary)
                    .getBeanDefinition());

            DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
            MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
            mappingConverter.setCustomConversions(conversions);
            String converterBeanName = BeanNameUtils.nameOf(beanName, MappingMongoConverter.class);
            registry.registerBeanDefinition(converterBeanName, BeanDefinitionBuilder
                    .genericBeanDefinition(MappingMongoConverter.class, () -> mappingConverter)
                    .setPrimary(primary)
                    .getBeanDefinition());

            MongoTemplate mongoTemplate = new MongoTemplate(factory, mappingConverter);
            String mongoTemplateBeanName = BeanNameUtils.nameOf(beanName, MongoTemplate.class);
            registry.registerBeanDefinition(mongoTemplateBeanName, BeanDefinitionBuilder
                    .genericBeanDefinition(MongoTemplate.class, () -> mongoTemplate)
                    .setPrimary(primary)
                    .getBeanDefinition());
        } catch (Throwable e) {
            throw new CommonRuntimeException(e, "create mongodb data source {} exception", beanName);
        }
    }

    private MongoMappingContext mongoMappingContext(MongoEntityClasses mongoEntityClasses, MongoProperties properties,
            MongoCustomConversions conversions) {
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        MongoMappingContext context = new MongoMappingContext();
        mapper.from(properties.isAutoIndexCreation()).to(context::setAutoIndexCreation);
        context.setInitialEntitySet(mongoEntityClasses.getClasses());
        Class<?> strategyClass = properties.getFieldNamingStrategy();
        if (strategyClass != null) {
            context.setFieldNamingStrategy((FieldNamingStrategy)BeanUtils.instantiateClass(strategyClass));
        }
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        return context;
    }

}
