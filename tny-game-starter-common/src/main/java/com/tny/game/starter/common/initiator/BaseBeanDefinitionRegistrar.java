package com.tny.game.starter.common.initiator;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.util.Set;

import static com.tny.game.starter.common.initiator.EnvironmentAide.*;


/**
 * <p>
 */
public abstract class BaseBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {


    protected Environment environment;

    protected String root;

    protected BaseBeanDefinitionRegistrar(String root) {
        this.root = root;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        Set<String> names = getNames(this.environment, this.root);
        for (String name : names) {
            loadBeanDefinition(name, registry);
        }
        if (!names.contains(DEFAULT_NAME_KEY))
            loadBeanDefinition(DEFAULT_NAME_KEY, registry);
    }

    protected abstract void loadBeanDefinition(String name, BeanDefinitionRegistry registry);
}
