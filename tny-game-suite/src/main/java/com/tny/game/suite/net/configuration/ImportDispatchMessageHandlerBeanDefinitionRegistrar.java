package com.tny.game.suite.net.configuration;

import com.tny.game.net.command.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-02 11:45
 */
public class ImportDispatchMessageHandlerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> names = getNames(environment, HANDLER_HEAD);
        for (String name : names) {
            String handlerHead = key(HANDLER_HEAD, name);
            String handlerClassName = environment.getProperty(key(handlerHead, "class"), DispatchMessageHandler.class.getName());
            if (!handlerClassName.equals(DispatchMessageHandler.class.getName()))
                continue;
            // 注册 DispatchMessageHandlerSetting
            String handlerSettingName = name + DispatchMessageHandlerSetting.class.getSimpleName();
            registry.registerBeanDefinition(handlerSettingName,
                    BeanDefinitionBuilder.genericBeanDefinition(DispatchMessageHandlerSetting.class,
                            () -> Binder.get(environment)
                                    .bind(handlerHead, DispatchMessageHandlerSetting.class)
                                    .orElseGet(DispatchMessageHandlerSetting::new))
                            .getBeanDefinition());

            // 注册 DispatchMessageHandler
            String handlerName = name + MessageHandler.class.getSimpleName();
            registry.registerBeanDefinition(handlerName,
                    BeanDefinitionBuilder.genericBeanDefinition(DispatchMessageHandler.class)
                            .addConstructorArgReference(handlerSettingName)
                            .getBeanDefinition());
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
