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