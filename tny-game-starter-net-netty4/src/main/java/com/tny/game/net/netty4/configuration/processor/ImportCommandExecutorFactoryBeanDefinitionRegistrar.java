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
package com.tny.game.net.netty4.configuration.processor;

import com.tny.game.boot.registrar.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.processor.forkjoin.*;
import com.tny.game.net.netty4.configuration.processor.forkjoin.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.Nonnull;

import static com.tny.game.boot.environment.EnvironmentAide.*;

/**
 * <p>
 */
public class ImportCommandExecutorFactoryBeanDefinitionRegistrar extends ImportConfigurationBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nonnull AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        SerialCommandExecutorProperties serialConfigure = loadProperties(SerialCommandExecutorProperties.class);
        loadBeanDefinition("default", serialConfigure.getSetting(), registry);
        serialConfigure.getSettings().forEach((name, setting) -> loadBeanDefinition(name, setting, registry));
    }

    private boolean loadBeanDefinition(String name, SerialCommandExecutorSetting setting, BeanDefinitionRegistry registry) {
        if (setting == null || !setting.isEnable()) {
            return false;
        }
        String beanName = getBeanName(name, CommandExecutorFactory.class);
        if (registry.containsBeanDefinition(beanName)) {
            // 双路注册收口：@Bean（含 @ConditionalOnMissingBean）先到先得，registrar 不再覆盖
            return false;
        }
        registry.registerBeanDefinition(beanName,
                BeanDefinitionBuilder.genericBeanDefinition(DefaultCommandExecutorFactory.class)
                        .addConstructorArgValue(setting)
                        .getBeanDefinition());
        return false;
    }

}
