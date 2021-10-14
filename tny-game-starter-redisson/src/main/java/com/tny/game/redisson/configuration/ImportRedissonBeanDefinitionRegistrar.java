package com.tny.game.redisson.configuration;

import com.tny.game.redisson.*;
import com.tny.game.redisson.codec.*;
import org.springframework.beans.factory.support.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class ImportRedissonBeanDefinitionRegistrar extends ImportRedisBeanDefinitionRegistrar {

	@Override
	protected <T> void doRegister(BeanDefinitionRegistry registry, Class<T> entityClass, String mimeType) {
		String codecName = entityClass.getSimpleName() + "ObjectCodecableCodec";
		registry.registerBeanDefinition(codecName, BeanDefinitionBuilder
				.genericBeanDefinition(ObjectCodecableCodec.class)
				.addConstructorArgValue(entityClass)
				.addConstructorArgValue(mimeType)
				.addConstructorArgReference("objectCodecService")
				.getBeanDefinition());
		TypedRedisson<?> typedRedisson = RedissonFactory.createTypedRedisson(entityClass);
		Class<TypedRedisson<?>> clazz = as(typedRedisson.getClass());
		registry.registerBeanDefinition(clazz.getSimpleName(),
				BeanDefinitionBuilder
						.genericBeanDefinition(clazz, () -> typedRedisson)
						.addPropertyReference("codec", codecName)
						.getBeanDefinition());
	}

}
