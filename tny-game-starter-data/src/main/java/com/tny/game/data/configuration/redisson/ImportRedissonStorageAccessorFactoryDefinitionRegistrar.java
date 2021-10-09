package com.tny.game.data.configuration.redisson;

import com.tny.game.boot.registrar.*;
import com.tny.game.data.redisson.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
public class ImportRedissonStorageAccessorFactoryDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

	private void registerRedissonStorageAccessorFactory(
			BeanDefinitionRegistry registry, RedissonStorageAccessorFactorySetting setting, String beanName) {
		RedissonStorageAccessorFactory factory = new RedissonStorageAccessorFactory(setting.getTableHead());
		registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
				.genericBeanDefinition(RedissonStorageAccessorFactory.class, () -> factory)
				.getBeanDefinition());
	}

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
		RedissonStorageAccessorFactoryProperties properties = loadProperties(RedissonStorageAccessorFactoryProperties.class);
		if (!properties.isEnable()) {
			return;
		}
		RedissonStorageAccessorFactorySetting defaultSetting = properties.getRedissonAccessor();
		if (defaultSetting != null) {
			registerRedissonStorageAccessorFactory(registry, defaultSetting,
					ifNotBlankElse(defaultSetting.getName(), RedissonStorageAccessorFactory.ACCESSOR_NAME));
		}
		properties.getRedissonAccessors().forEach((name, setting) -> registerRedissonStorageAccessorFactory(registry, setting, name));
	}

}
