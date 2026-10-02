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
