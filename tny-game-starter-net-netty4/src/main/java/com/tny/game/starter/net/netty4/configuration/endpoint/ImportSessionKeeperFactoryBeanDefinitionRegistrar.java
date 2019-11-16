package com.tny.game.starter.net.netty4.configuration.endpoint;

import com.tny.game.common.utils.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.starter.common.initiator.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.starter.common.initiator.EnvironmentAide.*;
import static com.tny.game.starter.net.netty4.configuration.NetEnvironmentAide.*;

/**
 * <p>
 */
public class ImportSessionKeeperFactoryBeanDefinitionRegistrar extends BaseBeanDefinitionRegistrar {

    protected ImportSessionKeeperFactoryBeanDefinitionRegistrar() {
        super(SESSION_KEEPER_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(this.root, name);
        String factoryClassName = this.environment.getProperty(key(keyHead, CLASS_NODE), CommonSessionKeeperFactory.class.getName());
        Class<SessionKeeperFactory> factoryClass = as(ExeAide.callUnchecked(() -> Class.forName(factoryClassName)).orElse(null));
        String factoryName = getBeanName(name, SessionKeeperFactory.class);
        registry.registerBeanDefinition(factoryName, BeanDefinitionBuilder
                .genericBeanDefinition(factoryClass,
                        () -> Binder.get(this.environment).bind(keyHead, factoryClass)
                                    .orElseGet(() -> ExeAide.callUnchecked(factoryClass::newInstance).orElse(null)))
                .getBeanDefinition());

    }

}
