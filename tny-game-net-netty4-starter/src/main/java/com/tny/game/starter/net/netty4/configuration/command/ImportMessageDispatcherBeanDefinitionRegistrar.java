package com.tny.game.starter.net.netty4.configuration.command;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.starter.common.initiator.*;
import com.tny.game.starter.net.netty4.spring.*;
import org.springframework.beans.factory.support.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.starter.common.environment.EnvironmentAide.*;
import static com.tny.game.starter.net.netty4.configuration.NetEnvironmentAide.*;

/**
 * <p>
 */
public class ImportMessageDispatcherBeanDefinitionRegistrar extends BaseBeanDefinitionRegistrar {

    protected ImportMessageDispatcherBeanDefinitionRegistrar() {
        super(MESSAGE_DISPATCHER_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(this.root, name);
        String dispatcherClassName = this.environment.getProperty(key(keyHead, CLASS_NODE), SuiteMessageDispatcher.class.getName());
        Class<MessageDispatcher> dispatcherClass = as(ExeAide.callUnchecked(() -> Class.forName(dispatcherClassName))
                .orElseThrow(() -> new IllegalArgumentException(format("class {} no found", dispatcherClassName))));
        String keyName = getBeanName(name, MessageDispatcher.class);
        registry.registerBeanDefinition(keyName,
                BeanDefinitionBuilder.genericBeanDefinition(dispatcherClass)
                        .addConstructorArgReference("appContext")
                        .addConstructorArgReference("endpointKeeperManager")
                        .getBeanDefinition());
    }

}
