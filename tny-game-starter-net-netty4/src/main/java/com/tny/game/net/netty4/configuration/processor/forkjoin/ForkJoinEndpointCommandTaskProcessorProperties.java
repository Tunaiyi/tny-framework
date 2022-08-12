/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.configuration.processor.forkjoin;

import com.google.common.collect.ImmutableMap;
import com.tny.game.net.command.processor.forkjoin.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.*;

import java.util.Map;

/**
 * 不主要加 @Configuration
 * 通过 ImportCommandTaskProcessorBeanDefinitionRegistrar 注册
 *
 * @author KGTny
 */
@ConditionalOnMissingBean(ForkJoinEndpointCommandTaskProcessorProperties.class)
@ConfigurationProperties(prefix = "tny.net.command.processor.forkjoin")
public class ForkJoinEndpointCommandTaskProcessorProperties {

    @NestedConfigurationProperty
    private ForkJoinEndpointCommandTaskProcessorSetting setting = new ForkJoinEndpointCommandTaskProcessorSetting()
            .setEnable(false);

    private Map<String, ForkJoinEndpointCommandTaskProcessorSetting> settings = ImmutableMap.of();

    public ForkJoinEndpointCommandTaskProcessorSetting getSetting() {
        return this.setting;
    }

    public ForkJoinEndpointCommandTaskProcessorProperties setSetting(
            ForkJoinEndpointCommandTaskProcessorSetting setting) {
        this.setting = setting;
        return this;
    }

    public Map<String, ForkJoinEndpointCommandTaskProcessorSetting> getSettings() {
        return this.settings;
    }

    public ForkJoinEndpointCommandTaskProcessorProperties setSettings(Map<String, ForkJoinEndpointCommandTaskProcessorSetting> settings) {
        this.settings = settings;
        return this;
    }

}