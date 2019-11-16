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
public class ImportTerminalKeeperFactoryBeanDefinitionRegistrar extends BaseBeanDefinitionRegistrar {

    protected ImportTerminalKeeperFactoryBeanDefinitionRegistrar() {
        super(TERMINAL_KEEPER_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(this.root, name);
        String factoryClassName = this.environment.getProperty(key(keyHead, CLASS_NODE), CommonTerminalKeeperFactory.class.getName());
        Class<TerminalKeeperFactory> factoryClass = as(ExeAide.callUnchecked(() -> Class.forName(factoryClassName)).orElse(null));
        String factoryName = getBeanName(name, TerminalKeeperFactory.class);
        registry.registerBeanDefinition(factoryName, BeanDefinitionBuilder.genericBeanDefinition(factoryClass,
                () -> Binder.get(this.environment)
                            .bind(keyHead, factoryClass)
                            .orElseGet(() -> ExeAide.callUnchecked(factoryClass::newInstance).orElse(null)))
                                                                          .getBeanDefinition());

    }

}
