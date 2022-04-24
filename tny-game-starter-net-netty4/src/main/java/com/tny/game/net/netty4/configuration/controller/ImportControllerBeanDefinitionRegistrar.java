package com.tny.game.net.netty4.configuration.controller;

import com.tny.game.boot.utils.*;
import com.tny.game.net.loader.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/13 2:13 下午
 */
public class ImportControllerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Set<Class<?>> classes = RpcControllerLoader.getControllerClasses();
        for (Class<?> controllerClass : classes) {
            registry.registerBeanDefinition(BeanNameUtils.lowerCamelName(controllerClass),
                    BeanDefinitionBuilder.genericBeanDefinition(controllerClass).getBeanDefinition());
        }
    }

}
