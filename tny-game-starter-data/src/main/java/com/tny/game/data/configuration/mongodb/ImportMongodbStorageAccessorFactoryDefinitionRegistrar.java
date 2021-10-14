package com.tny.game.data.configuration.mongodb;

import com.tny.game.boot.registrar.*;
import com.tny.game.data.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
public class ImportMongodbStorageAccessorFactoryDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

	private void registerMongodbStorageAccessorFactory(
			BeanDefinitionRegistry registry, MongodbStorageAccessorFactorySetting setting, String beanName) {
		MongodbStorageAccessorFactory factory = new MongodbStorageAccessorFactory();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(MongodbStorageAccessorFactory.class, () -> factory);
		builder.addPropertyReference("idConverter", setting.getIdConverter());
		builder.addPropertyReference("entityConverter", setting.getEntityConverter());
		if (StringUtils.isBlank(setting.getMongoTemplate())) {
			builder.addAutowiredProperty("mongoTemplate");
		} else {
			builder.addPropertyReference("mongoTemplate", setting.getMongoTemplate());
		}
		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
	}

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
		MongodbStorageAccessorFactoryProperties properties = loadProperties(MongodbStorageAccessorFactoryProperties.class);
		if (!properties.isEnable()) {
			return;
		}
		MongodbStorageAccessorFactorySetting defaultSetting = properties.getAccessor();
		if (defaultSetting != null) {
			registerMongodbStorageAccessorFactory(registry, defaultSetting, MongodbStorageAccessorFactory.ACCESSOR_NAME);
		}
		properties.getAccessors().forEach((name, setting) -> registerMongodbStorageAccessorFactory(registry, setting, name));
	}

}
