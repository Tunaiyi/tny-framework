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