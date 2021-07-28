package com.tny.game.redisson.configuration;

import com.tny.game.redisson.*;
import com.tny.game.redisson.codec.*;
import org.springframework.beans.factory.support.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class ImportRedissonBeanDefinitionRegistrar extends ImportRedisBeanDefinitionRegistrar {

    //    public static final Logger LOGGER = LoggerFactory.getLogger(ImportRedissonBeanDefinitionRegistrar.class);

    @Override
    protected <T> void doRegister(BeanDefinitionRegistry registry, Class<T> entityClass, String mimeType, String source) {
        String codecName = entityClass.getSimpleName() + "ObjectCodecableCodec";
        registry.registerBeanDefinition(codecName, BeanDefinitionBuilder
                .genericBeanDefinition(ObjectCodecableCodec.class)
                .addConstructorArgValue(entityClass)
                .addConstructorArgValue(mimeType)
                .addConstructorArgReference("objectCodecService")
                .getBeanDefinition());
        TypedRedisson<?> typedRedisson = RedissonFactory.createTypedRedisson(entityClass, source);
        Class<TypedRedisson<?>> clazz = as(typedRedisson.getClass());
        registry.registerBeanDefinition(clazz.getSimpleName(),
                BeanDefinitionBuilder
                        .genericBeanDefinition(clazz, () -> typedRedisson)
                        .addPropertyReference("codec", codecName)
                        .getBeanDefinition());
    }

}
