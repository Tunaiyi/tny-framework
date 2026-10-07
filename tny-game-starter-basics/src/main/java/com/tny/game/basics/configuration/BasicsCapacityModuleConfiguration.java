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

package com.tny.game.basics.configuration;

import com.tny.game.basics.item.capacity.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(BasicsCapacityModuleProperties.class)
@ConditionalOnProperty(name = BasicsPropertyConstants.BASICS_CAPACITY_MODULE_ENABLE, havingValue = "true")
public class BasicsCapacityModuleConfiguration {

    @Bean
    public CapacityDebugger capacityDebugger() {
        return new CapacityDebugger();
    }

    @Bean
    public CapacityService capacityService() {
        return new CapacityService();
    }

}
