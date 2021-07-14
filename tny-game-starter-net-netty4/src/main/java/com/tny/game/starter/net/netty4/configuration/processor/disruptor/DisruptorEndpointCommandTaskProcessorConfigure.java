package com.tny.game.starter.net.netty4.configuration.processor.disruptor;

import com.google.common.collect.ImmutableMap;
import com.tny.game.net.netty4.processor.disruptor.*;
import org.springframework.boot.context.properties.*;

import java.util.Map;

/**
 * @author KGTny
 */
@ConfigurationProperties(prefix = "tny.net.command.processor.disruptor")
public class DisruptorEndpointCommandTaskProcessorConfigure {

    @NestedConfigurationProperty
    private DisruptorEndpointCommandTaskProcessorSetting setting = new DisruptorEndpointCommandTaskProcessorSetting()
            .setEnable(false);

    private Map<String, DisruptorEndpointCommandTaskProcessorSetting> settings = ImmutableMap.of();

    public DisruptorEndpointCommandTaskProcessorSetting getSetting() {
        return this.setting;
    }

    public DisruptorEndpointCommandTaskProcessorConfigure setSetting(
            DisruptorEndpointCommandTaskProcessorSetting setting) {
        this.setting = setting;
        return this;
    }

    public Map<String, DisruptorEndpointCommandTaskProcessorSetting> getSettings() {
        return this.settings;
    }

    public DisruptorEndpointCommandTaskProcessorConfigure setSettings(Map<String, DisruptorEndpointCommandTaskProcessorSetting> settings) {
        this.settings = settings;
        return this;
    }

}