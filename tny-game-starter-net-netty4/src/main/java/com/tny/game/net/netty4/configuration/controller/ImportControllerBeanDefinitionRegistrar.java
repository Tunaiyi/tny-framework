/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
