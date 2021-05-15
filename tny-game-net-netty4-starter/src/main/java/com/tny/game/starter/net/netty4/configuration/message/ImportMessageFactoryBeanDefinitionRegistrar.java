package com.tny.game.starter.net.netty4.configuration.message;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.starter.common.initiator.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.starter.common.environment.EnvironmentAide.*;
import static com.tny.game.starter.net.netty4.configuration.NetEnvironmentAide.*;

/**
 * <p>
 */
public class ImportMessageFactoryBeanDefinitionRegistrar extends BaseBeanDefinitionRegistrar {

    protected ImportMessageFactoryBeanDefinitionRegistrar() {
        super(COMMAND_DISPATCHER_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(this.root, name);
        String factoryClassName = this.environment.getProperty(key(keyHead, CLASS_NODE), CommonMessageFactory.class.getName());
        Class<MessageFactory<?>> factoryClass = as(ExeAide.callUnchecked(() -> Class.forName(factoryClassName))
                .orElseThrow(() -> new IllegalArgumentException(format(" class {} not found", factoryClassName))));
        String keyName = getBeanName(name, MessageFactory.class);
        registry.registerBeanDefinition(keyName,
                BeanDefinitionBuilder.genericBeanDefinition(factoryClass, () -> Binder.get(this.environment)
                        .bind(keyHead, factoryClass)
                        .orElseGet(() -> ExeAide.callUnchecked(factoryClass::newInstance).orElse(null)))
                        .getBeanDefinition());
    }

}
