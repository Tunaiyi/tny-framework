package com.tny.game.net.netty4.configuration.processor;

import com.tny.game.boot.registrar.*;
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
		DisruptorEndpointCommandTaskProcessorProperties disruptorConfigure = loadProperties(DisruptorEndpointCommandTaskProcessorProperties.class);
		ForkJoinEndpointCommandTaskProcessorProperties forkJoinConfigure = loadProperties(ForkJoinEndpointCommandTaskProcessorProperties.class);
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
		String beanName = getBeanName(name, CommandTaskBoxProcessor.class);
		registry.registerBeanDefinition(beanName,
				BeanDefinitionBuilder.genericBeanDefinition(ForkJoinEndpointCommandTaskBoxProcessor.class)
						.addConstructorArgValue(setting)
						.getBeanDefinition());
		return false;
	}

	private boolean loadBeanDefinition(String name, DisruptorEndpointCommandTaskBoxProcessorSetting setting, BeanDefinitionRegistry registry) {
		if (setting == null || !setting.isEnable()) {
			return false;
		}
		String beanName = getBeanName(name, CommandTaskBoxProcessor.class);
		registry.registerBeanDefinition(beanName,
				BeanDefinitionBuilder.genericBeanDefinition(DisruptorEndpointCommandTaskBoxProcessor.class)
						.addConstructorArgValue(setting)
						.getBeanDefinition());
		return true;

	}

}
