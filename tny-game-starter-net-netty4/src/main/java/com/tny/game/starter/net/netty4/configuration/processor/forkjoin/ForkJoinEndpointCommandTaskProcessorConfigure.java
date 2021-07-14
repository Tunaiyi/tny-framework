package com.tny.game.starter.net.netty4.configuration.processor.forkjoin;

import com.google.common.collect.ImmutableMap;
import com.tny.game.net.command.processor.forkjoin.*;
import org.springframework.boot.context.properties.*;

import java.util.Map;

/**
 * @author KGTny
 */
@ConfigurationProperties(prefix = "tny.net.command.processor.forkjoin")
public class ForkJoinEndpointCommandTaskProcessorConfigure {

    @NestedConfigurationProperty
    private ForkJoinEndpointCommandTaskProcessorSetting setting = new ForkJoinEndpointCommandTaskProcessorSetting()
            .setEnable(false);

    private Map<String, ForkJoinEndpointCommandTaskProcessorSetting> settings = ImmutableMap.of();

    public ForkJoinEndpointCommandTaskProcessorSetting getSetting() {
        return this.setting;
    }

    public ForkJoinEndpointCommandTaskProcessorConfigure setSetting(
            ForkJoinEndpointCommandTaskProcessorSetting setting) {
        this.setting = setting;
        return this;
    }

    public Map<String, ForkJoinEndpointCommandTaskProcessorSetting> getSettings() {
        return this.settings;
    }

    public ForkJoinEndpointCommandTaskProcessorConfigure setSettings(Map<String, ForkJoinEndpointCommandTaskProcessorSetting> settings) {
        this.settings = settings;
        return this;
    }

}