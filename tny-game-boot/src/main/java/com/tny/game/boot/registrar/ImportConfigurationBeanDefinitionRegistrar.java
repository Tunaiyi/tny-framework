package com.tny.game.boot.registrar;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;

import javax.annotation.Nonnull;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/12/31 6:29 上午
 */
public class ImportConfigurationBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {

    protected Environment environment;

    protected BeanFactory beanFactory;

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(@Nonnull Environment environment) {
        this.environment = environment;
    }

    protected <P> P loadProperties(Class<P> propertiesClass) {
        ConfigurationProperties configurationProperties = propertiesClass.getAnnotation(ConfigurationProperties.class);
        Asserts.checkNotNull(configurationProperties, "{} @ConfigurationProperties annotation is null", propertiesClass);
        String keyHead = configurationProperties.prefix();
        if (StringUtils.isBlank(keyHead)) {
            keyHead = configurationProperties.value();
        }
        return loadProperties(keyHead, propertiesClass);
    }

    protected <P> P loadProperties(String keyHead, Class<P> propertiesClass) {
        return Binder.get(this.environment)
                .bind(keyHead, propertiesClass)
                .orElseGet(() -> ExeAide.callUnchecked(propertiesClass::newInstance)
                        .orElse(null));
    }

}
