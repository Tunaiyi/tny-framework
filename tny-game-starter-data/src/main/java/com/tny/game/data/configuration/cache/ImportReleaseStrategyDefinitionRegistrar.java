package com.tny.game.data.configuration.cache;

import com.tny.game.boot.registrar.*;
import com.tny.game.boot.utils.*;
import com.tny.game.data.cache.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
public class ImportReleaseStrategyDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
		ReleaseStrategyProperties properties = loadProperties(ReleaseStrategyProperties.class);
		if (properties.getStrategy() != null) {
			registerTimeoutReleaseStrategyFactory(registry, properties.getStrategy(),
					BeanNameUtils.lowerCamelName(TimeoutReleaseStrategyFactory.class));
		}
		properties.getStrategies().forEach((name, setting) -> registerTimeoutReleaseStrategyFactory(registry, setting, name));
	}

	private void registerTimeoutReleaseStrategyFactory(BeanDefinitionRegistry registry, TimeoutReleaseStrategySetting setting,
			String beanName) {
		TimeoutReleaseStrategyFactory<?, ?> factory = new TimeoutReleaseStrategyFactory<>(setting);
		registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
				.genericBeanDefinition(TimeoutReleaseStrategyFactory.class, () -> factory)
				.getBeanDefinition());
	}

}
