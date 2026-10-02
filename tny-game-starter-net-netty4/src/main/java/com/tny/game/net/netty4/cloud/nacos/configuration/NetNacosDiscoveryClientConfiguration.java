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
