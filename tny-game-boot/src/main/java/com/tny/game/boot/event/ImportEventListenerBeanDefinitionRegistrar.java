package com.tny.game.boot.event;

import com.tny.game.boot.utils.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.*;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/13 2:13 下午
 */
public class ImportEventListenerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Set<Class<?>> classes = EventHandlerLoader.getEventListenerClasses();
        for (Class<?> listenerClass : classes) {
            if (listenerClass.getAnnotation(Component.class) != null || listenerClass.getAnnotation(Service.class) != null) {
                continue;
            }
            registry.registerBeanDefinition(BeanNameUtils.lowerCamelName(listenerClass),
                    BeanDefinitionBuilder.genericBeanDefinition(listenerClass).getBeanDefinition());
        }
    }

}
