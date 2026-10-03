/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.redisson.configuration;

import com.tny.game.boot.utils.*;
import com.tny.game.redisson.*;
import com.tny.game.redisson.annotation.*;
import com.tny.game.redisson.codec.*;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.support.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class ImportRedissonBeanDefinitionRegistrar extends ImportRedisBeanDefinitionRegistrar {

    @Override
    protected <T> void doRegister(BeanDefinitionRegistry registry, Class<T> entityClass, String mimeType, boolean primary) {
        String codecName = entityClass.getSimpleName() + "ObjectCodecableCodec";
        registry.registerBeanDefinition(codecName, BeanDefinitionBuilder
                .genericBeanDefinition(ObjectCodableCodec.class)
                .addConstructorArgValue(entityClass)
                .addConstructorArgValue(mimeType)
                .setPrimary(primary)
                .addConstructorArgReference("objectCodecService")
                .getBeanDefinition());
        TypedRedisson<?> typedRedisson = RedissonFactory.createTypedRedisson(entityClass);
        Class<TypedRedisson<?>> clazz = as(typedRedisson.getClass());
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> typedRedisson);
        RedisObject redisObject = entityClass.getAnnotation(RedisObject.class);
        if (StringUtils.isNotBlank(redisObject.source())) {
            builder.addPropertyReference("redissonClient", BeanNameUtils.nameOf(redisObject.source(), RedissonClient.class));
        } else {
            builder.addAutowiredProperty("redissonClient");
        }
        registry.registerBeanDefinition(clazz.getSimpleName(), builder.addPropertyReference("codec", codecName).getBeanDefinition());
    }

}
