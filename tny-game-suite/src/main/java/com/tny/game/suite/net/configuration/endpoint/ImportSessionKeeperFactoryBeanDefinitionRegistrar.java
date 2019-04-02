package com.tny.game.suite.net.configuration.endpoint;

import com.tny.game.common.utils.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.suite.spring.*;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 */
public class ImportSessionKeeperFactoryBeanDefinitionRegistrar extends SuiteBeanDefinitionRegistrar {

    protected ImportSessionKeeperFactoryBeanDefinitionRegistrar() {
        super(SESSION_KEEPER_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(root, name);
        String factoryClassName = environment.getProperty(key(keyHead, CLASS_NODE), CommonSessionKeeperFactory.class.getName());
        Class<SessionKeeperFactory> factoryClass = as(ExeAide.callUnchecked(() -> Class.forName(factoryClassName)).orElse(null));
        String factoryName = getBeanName(name, SessionKeeperFactory.class);
        registry.registerBeanDefinition(factoryName, BeanDefinitionBuilder.genericBeanDefinition(factoryClass,
                () -> Binder.get(environment)
                        .bind(keyHead, factoryClass)
                        .orElseGet(() -> ExeAide.callUnchecked(factoryClass::newInstance).orElse(null)))
                .getBeanDefinition());

    }

}
