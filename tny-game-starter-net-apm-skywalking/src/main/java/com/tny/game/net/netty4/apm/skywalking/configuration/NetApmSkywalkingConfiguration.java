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
package com.tny.game.net.netty4.apm.skywalking.configuration;

import com.tny.game.net.netty4.apm.skywalking.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import static com.tny.game.net.netty4.apm.skywalking.SkywalkingPropertiesConstants.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2023/1/6 18:24
 */
// agent-core 为 compileOnly：无 -javaagent 环境缺该类时必须整体退避而非启动崩溃（net-boot-integration）
@ConditionalOnClass(org.apache.skywalking.apm.agent.core.context.ContextManager.class)
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        SkywalkingRpcMonitorProperties.class,
})
public class NetApmSkywalkingConfiguration {

    @Bean
    @ConditionalOnProperty(value = SKYWALKING_ENABLE, matchIfMissing = true, havingValue = "true")
    public SkywalkingRpcMonitorHandler skywalkingRpcMonitorHandler(SkywalkingRpcMonitorProperties setting) {
        return new SkywalkingRpcMonitorHandler(setting);
    }

}
