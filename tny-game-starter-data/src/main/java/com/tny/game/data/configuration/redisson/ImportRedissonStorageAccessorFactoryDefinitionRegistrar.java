package com.tny.game.data.configuration.redisson;

import com.tny.game.boot.registrar.*;
import com.tny.game.data.configuration.*;
import com.tny.game.data.redisson.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

import static com.tny.game.boot.utils.BeanNameUtils.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
@AutoConfigureBefore(DataAutoConfiguration.class)
public class ImportRedissonStorageAccessorFactoryDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

	private void registerRedissonStorageAccessorFactory(
			BeanDefinitionRegistry registry, RedissonStorageAccessorFactorySetting setting, String beanName) {
		RedissonStorageAccessorFactory factory = new RedissonStorageAccessorFactory(setting.getDataSource(), setting.getTableHead());
		registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
				.genericBeanDefinition(RedissonStorageAccessorFactory.class, () -> factory)
				.addPropertyReference("entityIdConverterFactory", setting.getIdConverterFactory())
				.getBeanDefinition());
	}

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
		RedissonStorageAccessorFactoryProperties properties = loadProperties(RedissonStorageAccessorFactoryProperties.class);
		if (!properties.isEnable()) {
			return;
		}
		RedissonStorageAccessorFactorySetting defaultSetting = properties.getAccessor();
		if (defaultSetting != null) {
			registerRedissonStorageAccessorFactory(registry, defaultSetting, RedissonStorageAccessorFactory.ACCESSOR_NAME);
		}
		properties.getAccessors().forEach((name, setting) -> {
			setting.setDataSource(name);
			registerRedissonStorageAccessorFactory(registry, setting, nameOf(name, RedissonStorageAccessorFactory.class));
		});
	}

}
