/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
