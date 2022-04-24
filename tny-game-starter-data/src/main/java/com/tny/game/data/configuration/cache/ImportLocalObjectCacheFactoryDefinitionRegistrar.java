package com.tny.game.data.configuration.cache;

import com.tny.game.boot.registrar.*;
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
public class ImportLocalObjectCacheFactoryDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    private void registerLocalObjectCacheFactory(BeanDefinitionRegistry registry, LocalObjectCacheFactorySetting setting, String beanName) {
        LocalObjectCacheFactory factory = new LocalObjectCacheFactory();
        registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
                .genericBeanDefinition(LocalObjectCacheFactory.class, () -> factory)
                .addPropertyReference("recycler", setting.getRecycler())
                .addPropertyReference("releaseStrategyFactory", setting.getReleaseStrategyFactory())
                .getBeanDefinition());
    }

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        LocalObjectCacheFactoriesProperties properties = loadProperties(LocalObjectCacheFactoriesProperties.class);
        if (!properties.isEnable()) {
            return;
        }
        LocalObjectCacheFactorySetting cacheSetting = properties.getCache();
        if (cacheSetting != null) {
            registerLocalObjectCacheFactory(registry, cacheSetting, LocalObjectCacheFactory.CACHE_NAME);
        }
        properties.getCaches().forEach((name, setting) -> registerLocalObjectCacheFactory(registry, setting, name));
    }

}
