package com.tny.game.data.mongodb.configuration;

import com.tny.game.data.mongodb.configuration.MongoEntityConverterFactory.*;
import com.tny.game.data.mongodb.loader.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.Set;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020/6/28 2:43 上午
 */

public class ImportConverterBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Set<Class<?>> classes = PersistObjectLoader.getConverterClasses();
        for (Class<?> entityClass : classes) {
            register(registry, entityClass, ConverterType.READ_CONVERTER);
            register(registry, entityClass, ConverterType.WRITE_CONVERTER);
        }
    }

    private void register(BeanDefinitionRegistry registry, Class<?> entityClass, ConverterType converterType) {
        Converter<?, ?> converter = MongoEntityConverterFactory.createConverter(entityClass, converterType);
        Class<Converter<?, ?>> clazz = as(converter.getClass());
        registry.registerBeanDefinition(clazz.getSimpleName(),
                BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> converter)
                        .addAutowiredProperty("objectConverter")
                        .getBeanDefinition());
    }

}
