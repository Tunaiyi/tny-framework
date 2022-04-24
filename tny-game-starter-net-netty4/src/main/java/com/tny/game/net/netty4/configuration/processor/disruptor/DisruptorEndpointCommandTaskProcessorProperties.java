package com.tny.game.net.netty4.configuration.processor.disruptor;

import com.google.common.collect.ImmutableMap;
import com.tny.game.net.netty4.processor.disruptor.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.*;

import java.util.Map;

/**
 * 不主要加 @Configuration
 * 通过 ImportCommandTaskProcessorBeanDefinitionRegistrar 注册
 *
 * @author KGTny
 */
@ConditionalOnMissingBean(DisruptorEndpointCommandTaskProcessorProperties.class)
@ConfigurationProperties(prefix = "tny.net.command.processor.disruptor")
public class DisruptorEndpointCommandTaskProcessorProperties {

    @NestedConfigurationProperty
    private DisruptorEndpointCommandTaskBoxProcessorSetting setting = new DisruptorEndpointCommandTaskBoxProcessorSetting()
            .setEnable(false);

    private Map<String, DisruptorEndpointCommandTaskBoxProcessorSetting> settings = ImmutableMap.of();

    public DisruptorEndpointCommandTaskBoxProcessorSetting getSetting() {
        return this.setting;
    }

    public DisruptorEndpointCommandTaskProcessorProperties setSetting(
            DisruptorEndpointCommandTaskBoxProcessorSetting setting) {
        this.setting = setting;
        return this;
    }

    public Map<String, DisruptorEndpointCommandTaskBoxProcessorSetting> getSettings() {
        return this.settings;
    }

    public DisruptorEndpointCommandTaskProcessorProperties setSettings(Map<String, DisruptorEndpointCommandTaskBoxProcessorSetting> settings) {
        this.settings = settings;
        return this;
    }

}