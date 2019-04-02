package com.tny.game.suite.net.configuration.command;

import com.tny.game.common.utils.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.suite.net.spring.*;
import com.tny.game.suite.spring.*;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 */
public class ImportMessageDispatcherBeanDefinitionRegistrar extends SuiteBeanDefinitionRegistrar {

    protected ImportMessageDispatcherBeanDefinitionRegistrar() {
        super(COMMAND_DISPATCHER_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(root, name);
        String dispatcherClassName = environment.getProperty(key(keyHead, CLASS_NODE), SuiteMessageDispatcher.class.getName());
        Class<MessageDispatcher> dispatcherClass = as(ExeAide.callUnchecked(() -> Class.forName(dispatcherClassName)).orElse(null));
        String keyName = getBeanName(name, MessageDispatcher.class);
        registry.registerBeanDefinition(keyName,
                BeanDefinitionBuilder.genericBeanDefinition(dispatcherClass,
                        () -> Binder.get(environment)
                                .bind(keyHead, dispatcherClass)
                                .orElseGet(() -> ExeAide.callUnchecked(dispatcherClass::newInstance).orElse(null)))
                        .addPropertyReference("appContext", "appContext")
                        .getBeanDefinition());
    }

}
