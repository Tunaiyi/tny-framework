package com.tny.game.data.configuration.storage;

import com.tny.game.boot.registrar.*;
import com.tny.game.data.storage.*;
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
		QueueObjectStorageFactoriesProperties properties = loadProperties(QueueObjectStorageFactoriesProperties.class);
		if (!properties.isEnable()) {
			return;
		}
		QueueObjectStorageFactorySetting queueSetting = properties.getQueueStorage();
		if (queueSetting != null) {
			registerQueueObjectStorageFactory(registry, queueSetting,
					ifNotBlankElse(queueSetting.getName(), QueueObjectStorageFactory.STORAGE_NAME));
		}
		properties.getQueueStorages().forEach((name, setting) -> registerQueueObjectStorageFactory(registry, setting, name));
	}

}
