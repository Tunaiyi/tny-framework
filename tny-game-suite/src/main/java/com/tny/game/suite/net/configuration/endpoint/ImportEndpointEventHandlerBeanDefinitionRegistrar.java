package com.tny.game.suite.net.configuration.endpoint;

import com.tny.game.common.utils.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;
import com.tny.game.suite.spring.*;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 */
public class ImportEndpointEventHandlerBeanDefinitionRegistrar extends SuiteBeanDefinitionRegistrar {

    private final static Class<CommonEndpointEventHandlerSetting> DEFAULT_ENDPOINT_EVENT_HANDLER_SETTING_CLASS = CommonEndpointEventHandlerSetting.class;

    protected ImportEndpointEventHandlerBeanDefinitionRegistrar() {
        super(EVENT_HANDLER_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(root, name);
        String settingClassName = environment.getProperty(key(keyHead, SETTING_CLASS_NODE), DEFAULT_ENDPOINT_EVENT_HANDLER_SETTING_CLASS.getName());
        Class<EndpointEventHandlerSetting> settingClass = as(ExeAide.callUnchecked(() -> Class.forName(settingClassName)).orElse(null));

        EndpointEventHandlerSetting setting = Binder.get(environment)
                .bind(keyHead, settingClass)
                .orElseGet(() -> ExeAide.callUnchecked(settingClass::newInstance).orElse(null));

        String handlerClassName = environment.getProperty(key(keyHead, CLASS_NODE), setting.getHandlerClass());
        String handlerName = getBeanName(name, EndpointEventHandler.class);
        Class<EndpointEventHandler> handlerClass = as(ExeAide.callUnchecked(() -> Class.forName(handlerClassName)).orElse(null));

        registry.registerBeanDefinition(handlerName, BeanDefinitionBuilder.genericBeanDefinition(handlerClass)
                .addConstructorArgValue(setting)
                .getBeanDefinition());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
