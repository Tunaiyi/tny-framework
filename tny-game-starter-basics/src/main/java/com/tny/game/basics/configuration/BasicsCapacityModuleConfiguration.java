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
@ConditionalOnProperty(name = BasicsPropertiesConstants.BASICS_CAPACITY_MODULE_ENABLE, havingValue = "true")
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
