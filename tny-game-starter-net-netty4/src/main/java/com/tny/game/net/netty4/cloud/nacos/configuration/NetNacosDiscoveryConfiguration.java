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

package com.tny.game.net.netty4.cloud.nacos.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.*;
import com.tny.game.net.netty4.cloud.*;
import com.tny.game.net.netty4.cloud.configuration.*;
import com.tny.game.net.netty4.cloud.nacos.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(NacosServiceRegistry.class)
@AutoConfigureAfter(NacosServiceRegistryAutoConfiguration.class)
@AutoConfigureBefore(NetDiscoveryConfiguration.class)
public class NetNacosDiscoveryConfiguration {

    @Bean
    @ConditionalOnBean(NacosDiscoveryProperties.class)
    public ServerGuideRegistrationFactory nacosServerGuideRegistrationFactory(
            NacosDiscoveryProperties properties,
            ObjectProvider<List<NacosRegistrationCustomizer>> registrationCustomizers,
            ObjectProvider<List<LocalServerNodeCustomizer>> localServerNodeCustomizer) {
        return new NacosServerGuideRegistrationFactory(properties,
                registrationCustomizers.getIfAvailable(),
                localServerNodeCustomizer.getIfAvailable());
    }

}

