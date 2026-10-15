/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.boot.registrar;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.Conventions;
import org.springframework.core.env.Environment;

import javax.annotation.Nonnull;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/12/31 6:29 上午
 */
public class ImportConfigurationBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {

    protected static final String CONFIGURATION_CLASS_LITE = "lite";

    protected static final String CONFIGURATION_CLASS_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");

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
                .orElseGet(() -> ExeAide.callUnchecked(() -> propertiesClass.getDeclaredConstructor().newInstance())
                        .orElse(null));
    }

}
