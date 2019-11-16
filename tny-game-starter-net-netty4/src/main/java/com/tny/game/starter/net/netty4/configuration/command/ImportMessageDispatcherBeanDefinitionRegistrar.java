package com.tny.game.starter.net.netty4.configuration.command;

import com.tny.game.common.utils.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.starter.common.initiator.*;
import com.tny.game.starter.net.netty4.spring.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.starter.common.initiator.EnvironmentAide.*;
import static com.tny.game.starter.net.netty4.configuration.NetEnvironmentAide.*;

/**
 * <p>
 */
public class ImportMessageDispatcherBeanDefinitionRegistrar extends BaseBeanDefinitionRegistrar {

    protected ImportMessageDispatcherBeanDefinitionRegistrar() {
        super(COMMAND_DISPATCHER_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(this.root, name);
        String dispatcherClassName = this.environment.getProperty(key(keyHead, CLASS_NODE), SuiteMessageDispatcher.class.getName());
        Class<MessageDispatcher> dispatcherClass = as(ExeAide.callUnchecked(() -> Class.forName(dispatcherClassName)).orElse(null));
        String keyName = getBeanName(name, MessageDispatcher.class);
        registry.registerBeanDefinition(keyName,
                BeanDefinitionBuilder.genericBeanDefinition(dispatcherClass,
                        () -> Binder.get(this.environment)
                                    .bind(keyHead, dispatcherClass)
                                    .orElseGet(() -> ExeAide.callUnchecked(dispatcherClass::newInstance).orElse(null)))
                                     .addPropertyReference("appContext", "appContext")
                                     .getBeanDefinition());
    }

}
