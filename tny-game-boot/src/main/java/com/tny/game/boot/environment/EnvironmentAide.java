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

package com.tny.game.boot.environment;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.*;

import java.util.*;
import java.util.stream.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public interface EnvironmentAide {

    String DEFAULT_NAME_KEY = "default";

    static String getBeanName(String beanName, Class<?> unitInterface) {
        if (beanName.equals(DEFAULT_NAME_KEY)) {
            return DEFAULT_NAME_KEY + unitInterface.getSimpleName();
        } else {
            return beanName;
        }
    }

    static String beanName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    static Set<String> getNames(Environment environment, String keyHead) {
        int nameIndex = StringUtils.split(keyHead, ".").length;
        List<String> propertiesAppNames = as(environment.getProperty(keyHead, List.class));
        if (propertiesAppNames != null) {
            return new HashSet<>(propertiesAppNames);
        } else {
            MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
            return StreamSupport.stream(propertySources.spliterator(), true)
                    .filter(EnumerablePropertySource.class::isInstance)
                    .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .filter(propName -> propName.startsWith(keyHead + "."))
                    .filter(propName -> !Objects.equals(propName, keyHead))
                    .map(propName -> StringUtils.split(propName, "."))
                    .filter(propNameWords -> propNameWords.length >= nameIndex)
                    .map(propNameWords -> propNameWords[nameIndex])
                    .collect(Collectors.toSet());
        }
    }

    static String key(String... nodes) {
        StringBuilder builder = new StringBuilder();
        for (String node : nodes) {
            if (builder.length() == 0) {
                builder.append(node);
            } else {
                builder.append(".").append(node);
            }
        }
        return builder.toString();
    }

}
