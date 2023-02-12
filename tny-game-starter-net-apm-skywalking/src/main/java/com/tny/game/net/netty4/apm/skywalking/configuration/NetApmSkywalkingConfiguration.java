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
package com.tny.game.net.netty4.apm.skywalking.configuration;

import com.tny.game.net.netty4.apm.skywalking.*;
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
