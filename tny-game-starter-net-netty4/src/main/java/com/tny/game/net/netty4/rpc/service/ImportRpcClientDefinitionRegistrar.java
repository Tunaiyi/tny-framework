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
import com.tny.game.net.netty4.rpc.configuration.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.rpc.setting.*;
import org.slf4j.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

/**
 * <p>
 */
public class ImportRpcClientDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    public static final Logger LOGGER = LoggerFactory.getLogger(ImportRpcClientDefinitionRegistrar.class);

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, @Nonnull BeanDefinitionRegistry registry) {
        RpcClusterProperties rpcProperties = loadProperties(RpcClusterProperties.class);
        for (RpcClusterSetting clusterSetting : rpcProperties.getServices()) {
            registerRpcConnector(registry, clusterSetting);
        }
    }

    private <T> void registerRpcConnector(BeanDefinitionRegistry registry, RpcClusterSetting setting) {
        String beanName = BeanNameUtils.lowerCamelName(setting.serviceName() + RpcConnectorFactory.class.getSimpleName());
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(RpcConnectorFactory.class)
                .addAutowiredProperty("appContext")
                .addPropertyValue("setting", setting);
        if (setting.isHasGuide()) {
            builder.addPropertyReference("clientGuide", setting.getGuide());
        } else {
            builder.addPropertyReference("clientGuide", "rpcClientGuide");
        }
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

}
