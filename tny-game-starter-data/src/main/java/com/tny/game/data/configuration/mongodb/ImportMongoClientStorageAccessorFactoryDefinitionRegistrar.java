package com.tny.game.data.configuration.mongodb;

import com.tny.game.boot.registrar.*;
import com.tny.game.data.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import javax.annotation.Nonnull;

import static com.tny.game.boot.utils.BeanNameUtils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
public class ImportMongoClientStorageAccessorFactoryDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

	private void registerMongoClientStorageAccessorFactory(
			BeanDefinitionRegistry registry, MongoClientStorageAccessorFactorySetting setting, String beanName) {
		MongoClientStorageAccessorFactory factory = new MongoClientStorageAccessorFactory();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(MongoClientStorageAccessorFactory.class, () -> factory);
		builder.addPropertyReference("idConverter", setting.getIdConverter());
		builder.addPropertyReference("entityConverter", setting.getEntityConverter());
		if (StringUtils.isBlank(setting.getDataSource())) {
			builder.addAutowiredProperty("databaseFactory");
		} else {
			builder.addPropertyReference("databaseFactory", nameOf(setting.getDataSource(), MongoDatabaseFactory.class));
		}
		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
	}

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
		MongoClientStorageAccessorFactoryProperties properties = loadProperties(MongoClientStorageAccessorFactoryProperties.class);
		if (!properties.isEnable()) {
			return;
		}
		MongoClientStorageAccessorFactorySetting defaultSetting = properties.getAccessor();
		if (defaultSetting != null) {
			registerMongoClientStorageAccessorFactory(registry, defaultSetting, MongoClientStorageAccessorFactory.ACCESSOR_NAME);
		}
		properties.getAccessors().forEach((name, setting) ->
				registerMongoClientStorageAccessorFactory(registry, setting, nameOf(name, MongoClientStorageAccessorFactory.class)));
	}

}
