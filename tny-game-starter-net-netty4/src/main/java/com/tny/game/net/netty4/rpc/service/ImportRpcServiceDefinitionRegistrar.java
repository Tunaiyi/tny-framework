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

package com.tny.game.net.netty4.rpc.service;

import com.tny.game.boot.registrar.*;
import com.tny.game.boot.utils.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.rpc.loader.*;
import org.slf4j.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

/**
 * <p>
 */
public class ImportRpcServiceDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    public static final Logger LOGGER = LoggerFactory.getLogger(ImportRpcServiceDefinitionRegistrar.class);

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // BFPP 解析期不再 getBean：代理实例依赖图延迟到 bean 创建阶段解析，
        // 保证 @PostConstruct/@Autowired/AOP 与集合依赖完备（net-boot-integration"装配期不产生半初始化单例"）
        for (Class<?> serviceClass : RpcServiceLoader.getServiceClasses()) {
            registerRpcInstance(registry, serviceClass);
        }
    }

    private <T> void registerRpcInstance(BeanDefinitionRegistry registry, Class<T> serviceClass) {
        LOGGER.debug("Register RpcService instance : {}", serviceClass);
        String beanName = BeanNameUtils.lowerCamelName(serviceClass);
        BeanDefinition definition = BeanDefinitionBuilder
                .genericBeanDefinition(serviceClass, () -> beanFactory.getBean(RpcRemoteInstanceFactory.class).create(serviceClass))
                .getBeanDefinition();
        definition.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
        registry.registerBeanDefinition(beanName, definition);
    }

}
