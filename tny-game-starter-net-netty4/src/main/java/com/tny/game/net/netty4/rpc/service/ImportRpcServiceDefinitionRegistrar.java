package com.tny.game.net.netty4.rpc.service;

import com.tny.game.boot.registrar.*;
import com.tny.game.boot.utils.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.rpc.loader.*;
import org.slf4j.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

/**
 * <p>
 */
public class ImportRpcServiceDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    public static final Logger LOGGER = LoggerFactory.getLogger(ImportRpcServiceDefinitionRegistrar.class);

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RpcRemoteInstanceFactory factory = beanFactory.getBean(RpcRemoteInstanceFactory.class);
        for (Class<?> serviceClass : RpcServiceLoader.getServiceClasses()) {
            registerRpcInstance(registry, factory, serviceClass);
        }
    }

    private <T> void registerRpcInstance(BeanDefinitionRegistry registry, RpcRemoteInstanceFactory factory, Class<T> serviceClass) {
        T rpcInstance = factory.create(serviceClass);
        LOGGER.debug("Register RpcService instance : {}", serviceClass);
        String beanName = BeanNameUtils.lowerCamelName(serviceClass);
        registry.registerBeanDefinition(beanName, BeanDefinitionBuilder
                .genericBeanDefinition(serviceClass, () -> rpcInstance)
                .getBeanDefinition());
    }

}
