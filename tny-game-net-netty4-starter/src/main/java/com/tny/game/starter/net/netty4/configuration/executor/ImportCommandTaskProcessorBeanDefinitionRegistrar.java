package com.tny.game.starter.net.netty4.configuration.executor;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.command.executor.*;
import com.tny.game.starter.common.initiator.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.starter.common.environment.EnvironmentAide.*;
import static com.tny.game.starter.net.netty4.configuration.NetEnvironmentAide.*;

/**
 * <p>
 */
public class ImportCommandTaskProcessorBeanDefinitionRegistrar<E extends CommandTaskProcessor> extends BaseBeanDefinitionRegistrar {

    protected ImportCommandTaskProcessorBeanDefinitionRegistrar() {
        super(COMMAND_TASK_PROCESSOR_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(this.root, name);
        String executorClassName = this.environment.getProperty(key(keyHead, CLASS_NODE), EndpointCommandTaskProcessor.class.getName());
        Class<E> executorClass = as(ExeAide.callUnchecked(() -> Class.forName(executorClassName)).orElse(null));
        String keyName = getBeanName(name, CommandTaskProcessor.class);
        registry.registerBeanDefinition(keyName,
                BeanDefinitionBuilder.genericBeanDefinition(executorClass,
                        () -> Binder.get(this.environment)
                                .bind(keyHead, executorClass)
                                .orElseGet(() -> ExeAide.callUnchecked(executorClass::newInstance).orElse(null)))
                        .getBeanDefinition());
    }

    protected boolean isDefaultClass() {
        return false;
    }

}
