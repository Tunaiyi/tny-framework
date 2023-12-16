/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
        registry.registerBeanDefinition(beanName,
                BeanDefinitionBuilder.genericBeanDefinition(DefaultCommandExecutorFactory.class)
                        .addConstructorArgValue(setting)
                        .getBeanDefinition());
        return false;
    }

}
