package com.tny.game.data.configuration;

import com.tny.game.boot.registrar.*;
import com.tny.game.boot.utils.*;
import com.tny.game.common.type.*;
import com.tny.game.data.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
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
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
		Set<Class<?>> registeredClasses = new HashSet<>();
		for (CacheScheme scheme : DataClassLoader.getAllCacheSchemeSchemes()) {
			Class<?> objectClass = scheme.getCacheClass();
			if (!registeredClasses.add(objectClass)) {
				continue;
			}
			EntityKeyMakerFactory entityKeyMakerFactory = beanFactory.getBean(scheme.keyMakerFactory(), EntityKeyMakerFactory.class);
			EntityKeyMaker<?, ?> keyMaker = entityKeyMakerFactory.createMaker(scheme);

			ObjectCacheFactory objectCacheFactory = beanFactory.getBean(scheme.cacheFactory(), ObjectCacheFactory.class);
			ObjectCache<?, ?> objectCache = objectCacheFactory.createCache(scheme, as(keyMaker));

			ObjectStorageFactory objectStorageFactory = beanFactory.getBean(scheme.storageFactory(), ObjectStorageFactory.class);
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
