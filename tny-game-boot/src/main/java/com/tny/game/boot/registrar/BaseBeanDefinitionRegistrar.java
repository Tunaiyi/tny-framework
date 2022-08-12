/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.boot.registrar;

import com.tny.game.boot.environment.*;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.*;

import java.util.Set;

import static com.tny.game.boot.environment.EnvironmentAide.*;

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
    public void setEnvironment(@Nullable Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        Set<String> names = EnvironmentAide.getNames(this.environment, this.root);
        for (String name : names) {
            loadBeanDefinition(name, registry);
        }
        if (!names.contains(DEFAULT_NAME_KEY)) {
            loadBeanDefinition(DEFAULT_NAME_KEY, registry);
        }
    }

    protected abstract void loadBeanDefinition(String name, BeanDefinitionRegistry registry);

}
