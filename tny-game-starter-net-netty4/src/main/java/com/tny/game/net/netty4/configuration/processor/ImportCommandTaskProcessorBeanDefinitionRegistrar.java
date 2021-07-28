package com.tny.game.net.netty4.configuration.processor;

import com.tny.game.boot.initiator.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.processor.forkjoin.*;
import com.tny.game.net.netty4.configuration.processor.disruptor.*;
import com.tny.game.net.netty4.configuration.processor.forkjoin.*;
import com.tny.game.net.netty4.processor.disruptor.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import static com.tny.game.boot.environment.EnvironmentAide.*;

/**
 * <p>
 */
public class ImportCommandTaskProcessorBeanDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        DisruptorEndpointCommandTaskProcessorConfigure disruptorConfigure = loadProperties(DisruptorEndpointCommandTaskProcessorConfigure.class);
        ForkJoinEndpointCommandTaskProcessorConfigure forkJoinConfigure = loadProperties(ForkJoinEndpointCommandTaskProcessorConfigure.class);
        boolean defaultProcessorInit = loadBeanDefinition("default", disruptorConfigure.getSetting(), registry);
        if (!defaultProcessorInit) {
            loadBeanDefinition("default", forkJoinConfigure.getSetting(), registry);
        }
        disruptorConfigure.getSettings().forEach((name, setting) -> loadBeanDefinition(name, setting, registry));
        forkJoinConfigure.getSettings().forEach((name, setting) -> loadBeanDefinition(name, setting, registry));
    }

    private boolean loadBeanDefinition(String name, ForkJoinEndpointCommandTaskProcessorSetting setting, BeanDefinitionRegistry registry) {
        if (setting == null || !setting.isEnable()) {
            return false;
        }
        String beanName = getBeanName(name, CommandTaskProcessor.class);
        registry.registerBeanDefinition(beanName,
                BeanDefinitionBuilder.genericBeanDefinition(ForkJoinEndpointCommandTaskProcessor.class)
                        .addConstructorArgValue(setting)
                        .getBeanDefinition());
        return false;
    }

    private boolean loadBeanDefinition(String name, DisruptorEndpointCommandTaskProcessorSetting setting, BeanDefinitionRegistry registry) {
        if (setting == null || !setting.isEnable()) {
            return false;
        }
        String beanName = getBeanName(name, CommandTaskProcessor.class);
        registry.registerBeanDefinition(beanName,
                BeanDefinitionBuilder.genericBeanDefinition(DisruptorEndpointCommandTaskProcessor.class)
                        .addConstructorArgValue(setting)
                        .getBeanDefinition());
        return true;

    }

}
