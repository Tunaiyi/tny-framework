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
@ConditionalOnMissingBean(SerialCommandExecutorProperties.class)
@ConfigurationProperties(prefix = "tny.net.command.executor.serial")
public class SerialCommandExecutorProperties {

    @NestedConfigurationProperty
    private SerialCommandExecutorSetting setting = new SerialCommandExecutorSetting()
            .setEnable(false);

    private Map<String, SerialCommandExecutorSetting> settings = ImmutableMap.of();

    public SerialCommandExecutorSetting getSetting() {
        return this.setting;
    }

    public SerialCommandExecutorProperties setSetting(
            SerialCommandExecutorSetting setting) {
        this.setting = setting;
        return this;
    }

    public Map<String, SerialCommandExecutorSetting> getSettings() {
        return this.settings;
    }

    public SerialCommandExecutorProperties setSettings(Map<String, SerialCommandExecutorSetting> settings) {
        this.settings = settings;
        return this;
    }

}