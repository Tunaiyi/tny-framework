package com.tny.game.suite.net.configuration.session;

import com.tny.game.net.endpoint.*;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

import static com.tny.game.suite.net.configuration.NetConfigurationAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-02 11:45
 */
public class ImportAbstactSessionKeeperBeanDefinitionRegistrar<F extends SessionKeeperFactory, S extends SessionKeeperSetting>
        implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    private Class<F> factoryClass;

    private Class<S> settingClass;

    private boolean defaultBean = false;

    public ImportAbstactSessionKeeperBeanDefinitionRegistrar(Class<F> factoryClass, Class<S> settingClass) {
        this(factoryClass, settingClass, false);

    }

    public ImportAbstactSessionKeeperBeanDefinitionRegistrar(Class<F> factoryClass, Class<S> settingClass, boolean defaultBean) {
        this.factoryClass = factoryClass;
        this.settingClass = settingClass;
        this.defaultBean = defaultBean;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> names = getNames(environment, SESSION_HEAD);
        String factoryName = factoryClass.getSimpleName();
        for (String name : names) {
            String sessionHead = key(SESSION_HEAD, name);
            String factoryClassName = environment.getProperty(key(sessionHead, FACTORY_KEY),
                    defaultBean ? factoryName : null);
            if (!factoryName.equals(factoryClassName))
                continue;
            String settingName = name + SessionKeeperSetting.class;
            registry.registerBeanDefinition(settingName,
                    BeanDefinitionBuilder.genericBeanDefinition(settingClass,
                            () -> Binder.get(environment)
                                    .bind(sessionHead, settingClass)
                                    .orElseCreate(settingClass))
                            .addPropertyValue("name", name)
                            .getBeanDefinition());
        }

        registry.registerBeanDefinition(beanName(this.factoryClass),
                BeanDefinitionBuilder.genericBeanDefinition(factoryClass)
                        .getBeanDefinition());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
