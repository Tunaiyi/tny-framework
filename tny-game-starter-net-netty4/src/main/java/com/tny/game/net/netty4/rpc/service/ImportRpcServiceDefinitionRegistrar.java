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
