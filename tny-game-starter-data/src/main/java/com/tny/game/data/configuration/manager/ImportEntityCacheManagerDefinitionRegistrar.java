package com.tny.game.data.configuration.manager;

import com.tny.game.boot.registrar.*;
import com.tny.game.boot.utils.*;
import com.tny.game.common.type.*;
import com.tny.game.data.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:42 下午
 */
public class ImportEntityCacheManagerDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar implements BeanFactoryAware {

	private final static DynamicEntityCacheManagerFactory entityCacheManagerFactory = new DynamicEntityCacheManagerFactory();

	private BeanFactory beanFactory;

	// 实现BeanFactoryAware的方法，设置BeanFactory
	@Override
	public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private String beamName(String name, String defaultName) {
		if (StringUtils.isBlank(name)) {
			return defaultName;
		}
		return name;
	}

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
		EntityCacheManagerProperties properties = loadProperties(EntityCacheManagerProperties.class);
		Set<Class<?>> registeredClasses = new HashSet<>();
		for (EntityScheme scheme : EntitySchemeLoader.getAllCacheSchemes()) {
			Class<?> objectClass = scheme.getCacheClass();
			if (!registeredClasses.add(objectClass)) {
				continue;
			}
			EntityKeyMakerFactory entityKeyMakerFactory = beanFactory.getBean(
					beamName(scheme.keyMakerFactory(), properties.getKeyMakerFactory()), EntityKeyMakerFactory.class);
			EntityKeyMaker<?, ?> keyMaker = entityKeyMakerFactory.createMaker(scheme);

			ObjectCacheFactory objectCacheFactory = beanFactory.getBean(
					beamName(scheme.cacheFactory(), properties.getCacheFactory()), ObjectCacheFactory.class);
			ObjectCache<?, ?> objectCache = objectCacheFactory.createCache(scheme, as(keyMaker));

			ObjectStorageFactory objectStorageFactory = beanFactory.getBean(
					beamName(scheme.storageFactory(), properties.getStorageFactory()), ObjectStorageFactory.class);
			ObjectStorage<?, ?> objectStorage = objectStorageFactory.createStorage(scheme, as(keyMaker));

			Class<? extends Comparable<?>> keyClass = keyMaker.getKeyClass();
			if (keyClass.isPrimitive()) {
				keyClass = as(Wrapper.getWrapper(keyClass));
			}
			EntityCacheManager<?, ?> entityCacheManager = entityCacheManagerFactory.createCache(keyClass, scheme.getEntityClass());

			String managerBeanName = BeanNameUtils.lowerCamelName(objectClass.getSimpleName() + EntityCacheManager.class.getSimpleName());
			registry.registerBeanDefinition(managerBeanName, BeanDefinitionBuilder
					.genericBeanDefinition(EntityCacheManager.class, () -> entityCacheManager)
					.addPropertyValue("cache", objectCache)
					.addPropertyValue("keyMaker", keyMaker)
					.addPropertyValue("storage", objectStorage)
					.addPropertyValue("currentLevel", scheme.concurrencyLevel())
					.getBeanDefinition());
		}
	}

}
