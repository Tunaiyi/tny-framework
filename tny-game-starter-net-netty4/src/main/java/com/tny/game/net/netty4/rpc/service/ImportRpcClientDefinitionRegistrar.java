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
        RpcClustersProperties rpcProperties = loadProperties(RpcClustersProperties.class);
        for (RpcClusterSetting clusterSetting : rpcProperties.getClusters()) {
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
