package com.tny.game.suite.net.configuration.message;

import com.tny.game.common.utils.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.suite.spring.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 */
public class ImportMessageFactoryBeanDefinitionRegistrar extends SuiteBeanDefinitionRegistrar {

    protected ImportMessageFactoryBeanDefinitionRegistrar() {
        super(COMMAND_DISPATCHER_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(root, name);
        String factoryClassName = environment.getProperty(key(keyHead, CLASS_NODE), CommonMessageFactory.class.getName());
        Class<MessageFactory> factoryClass = as(ExeAide.callUnchecked(() -> Class.forName(factoryClassName)).orElse(null));
        String keyName = getBeanName(name, MessageFactory.class);
        registry.registerBeanDefinition(keyName,
                BeanDefinitionBuilder.genericBeanDefinition(factoryClass,
                        () -> Binder.get(environment)
                                    .bind(keyHead, factoryClass)
                                    .orElseGet(() -> ExeAide.callUnchecked(factoryClass::newInstance).orElse(null)))
                                     .getBeanDefinition());
    }

}
