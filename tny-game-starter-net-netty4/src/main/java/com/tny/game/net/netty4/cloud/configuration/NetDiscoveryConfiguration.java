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

package com.tny.game.net.netty4.cloud.configuration;

import com.tny.game.net.netty4.cloud.*;
import com.tny.game.net.netty4.cloud.nacos.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.cloud.client.serviceregistry.*;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ServiceRegistry.class)
public class NetDiscoveryConfiguration {

    @Bean
    @ConditionalOnBean({ServiceRegistry.class, ServerGuideRegistrationFactory.class})
    public NetAutoServiceRegister netAutoServiceRegister(
            ServiceRegistry<Registration> serviceRegistry, List<ServerGuideRegistrationFactory> registrationFactory) {
        return new NetAutoServiceRegister(serviceRegistry, registrationFactory);
    }

}
