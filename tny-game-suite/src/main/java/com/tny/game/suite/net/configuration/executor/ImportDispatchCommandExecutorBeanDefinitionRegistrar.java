package com.tny.game.suite.net.configuration.executor;

import com.tny.game.net.command.executor.DispatchCommandExecutor;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;
import java.util.function.Supplier;

import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-02 11:45
 */
public abstract class ImportDispatchCommandExecutorBeanDefinitionRegistrar<E extends DispatchCommandExecutor> implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    private Class<E> executorClass;

    private Supplier<E> executorCreator;

    protected ImportDispatchCommandExecutorBeanDefinitionRegistrar(Class<E> executorClass, Supplier<E> executorCreator) {
        this.executorClass = executorClass;
        this.executorCreator = executorCreator;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> names = getNames(environment, EXECUTOR_HEAD);
        for (String name : names) {
            String executorHead = key(EXECUTOR_HEAD, name);
            String executorClassName = environment.getProperty(key(executorHead, "class"),
                    isDefaultClass() ? executorClass.getName() : null);
            String className = executorClass.getName();
            if (!className.equals(executorClassName))
                continue;
            // 注册 PerTunnelDispatchCommandExecutor
            String handlerName = name + DispatchCommandExecutor.class.getSimpleName();
            registry.registerBeanDefinition(handlerName,
                    BeanDefinitionBuilder.genericBeanDefinition(executorClass,
                            () -> Binder.get(environment)
                                    .bind(executorHead, executorClass)
                                    .orElseGet(this.executorCreator))
                            .getBeanDefinition());
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected boolean isDefaultClass() {
        return false;
    }
}
