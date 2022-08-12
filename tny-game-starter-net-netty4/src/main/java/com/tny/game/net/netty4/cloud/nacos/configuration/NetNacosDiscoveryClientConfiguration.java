/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.cloud.nacos.configuration;

import com.alibaba.cloud.nacos.*;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.tny.game.net.netty4.cloud.nacos.*;
import com.tny.game.net.relay.cluster.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 1:29 下午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(NacosDiscoveryProperties.class)
@AutoConfigureAfter(NacosDiscoveryAutoConfiguration.class)
public class NetNacosDiscoveryClientConfiguration {

    @Bean
    @ConditionalOnBean(NacosDiscoveryProperties.class)
    public ServeNodeClient serveNodeClient(NacosDiscoveryProperties properties, NacosServiceManager nacosServiceManager) {
        return new NacosServeNodeClient(properties, nacosServiceManager);
    }

}
