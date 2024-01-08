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

package com.tny.game.net.netty4.relay.cluster;

import com.tny.game.boot.registrar.*;
import com.tny.game.net.netty4.relay.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

import static com.tny.game.common.lifecycle.unit.UnitNames.*;

/**
 * <p>
 */
public class ImportRelayServeClusterBootstrapDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        SpringRelayClustersProperties properties = loadProperties(SpringRelayClustersProperties.class);
        for (SpringRelayServeClusterSetting setting : properties.getClusters()) {
            registerRelayServeClusterContext(setting, registry);
        }
    }

    private void registerRelayServeClusterContext(SpringRelayServeClusterSetting setting, BeanDefinitionRegistry registry) {
        NettyRemoteServeClusterContext clusterContext = new NettyRemoteServeClusterContext(setting);
        String name = unitName(setting.serviceName(), NettyRemoteServeClusterContext.class);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(NettyRemoteServeClusterContext.class, () -> clusterContext)
                .addPropertyReference("clientGuide", setting.getClientGuide());
        if (setting.isHasServeInstanceAllotStrategy()) {
            builder.addPropertyReference("serveInstanceAllotStrategy", setting.getServeInstanceAllotStrategy());
        }
        if (setting.isHasRelayLinkAllotStrategy()) {
            builder.addPropertyReference("relayLinkAllotStrategy", setting.getRelayLinkAllotStrategy());
        }
        registry.registerBeanDefinition(name, builder.getBeanDefinition());
    }

}
