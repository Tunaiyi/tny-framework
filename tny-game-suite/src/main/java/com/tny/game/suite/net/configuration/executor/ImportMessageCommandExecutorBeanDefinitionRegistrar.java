package com.tny.game.suite.net.configuration.executor;

import com.tny.game.common.utils.*;
import com.tny.game.net.command.executor.*;
import com.tny.game.suite.spring.*;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 */
public class ImportMessageCommandExecutorBeanDefinitionRegistrar<E extends MessageCommandExecutor> extends SuiteBeanDefinitionRegistrar {

    protected ImportMessageCommandExecutorBeanDefinitionRegistrar() {
        super(COMMAND_EXECUTOR_HEAD);
    }

    @Override
    protected void loadBeanDefinition(String name, BeanDefinitionRegistry registry) {
        String keyHead = key(root, name);
        String executorClassName = environment.getProperty(key(keyHead, CLASS_NODE), EndpointWorkerMessageCommandExecutor.class.getName());
        Class<E> executorClass = as(ExeAide.callUnchecked(() -> Class.forName(executorClassName)).orElse(null));
        String keyName = getBeanName(name, MessageCommandExecutor.class);
        registry.registerBeanDefinition(keyName,
                BeanDefinitionBuilder.genericBeanDefinition(executorClass,
                        () -> Binder.get(environment)
                                .bind(keyHead, executorClass)
                                .orElseGet(() -> ExeAide.callUnchecked(executorClass::newInstance).orElse(null)))
                        .getBeanDefinition());
    }

    protected boolean isDefaultClass() {
        return false;
    }

}
