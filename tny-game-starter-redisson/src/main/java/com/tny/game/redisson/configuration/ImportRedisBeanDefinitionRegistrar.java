/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.redisson.configuration;

import com.tny.game.codec.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.redisson.*;
import com.tny.game.redisson.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * <p>
 */
public abstract class ImportRedisBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata annotationMetadata, @Nonnull BeanDefinitionRegistry registry) {
        for (Class<?> entityClass : RedisObjectClassLoader.getAllClasses()) {
            RedisObject source = entityClass.getAnnotation(RedisObject.class);
            if (source == null) {
                continue;
            }
            register(registry, entityClass, source, true);
        }
        for (RedisObjectRegistrar registrar : RedisObjectClassLoader.getAllRegistrars()) {
            register(registry, registrar.value(), registrar.object(), false);
        }
    }

    private void register(BeanDefinitionRegistry registry, Class<?> entityClass, RedisObject source, boolean primary) {
        Codable codecable = source.codec();
        String mimeType = MimeTypeAide.getMimeType(codecable);
        if (Objects.equals(mimeType, MimeTypeAide.NONE)) {
            codecable = entityClass.getAnnotation(Codable.class);
            Asserts.checkNotNull(codecable, "{} is not {} annotation", entityClass, Codable.class);
            mimeType = MimeTypeAide.getMimeType(codecable);
        }
        Asserts.checkArgument(StringUtils.isNotBlank(mimeType), "{} mimeType must not blank {} ", entityClass, mimeType);
        doRegister(registry, entityClass, mimeType, primary);
    }

    protected abstract <T> void doRegister(BeanDefinitionRegistry registry, Class<T> entityClass, String mimeType, boolean primary);

}
