package com.tny.game.common.boot.initiator;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/12/31 6:29 上午
 */
public class ConfigurationImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    @Resource
    protected Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected <P> P loadProperties(Class<P> propertiesClass) {
        ConfigurationProperties configurationProperties = propertiesClass.getAnnotation(ConfigurationProperties.class);
        Asserts.checkNotNull(configurationProperties, "{} @ConfigurationProperties annotation is null", propertiesClass);
        String keyHead = configurationProperties.value();
        if (StringUtils.isBlank(keyHead)) {
            keyHead = configurationProperties.prefix();
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
