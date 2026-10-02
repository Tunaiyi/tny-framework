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

package com.tny.game.common.lifecycle.unit;

import com.tny.game.common.lifecycle.unit.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/18 2:33 下午
 */
public final class UnitNames {

    public static final String DEFAULT_HEAD = "default";

    private UnitNames() {
    }

    public static String defaultName(Class<?> clazz) {
        return DEFAULT_HEAD + clazz.getSimpleName();
    }

    public static String lowerCamelName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static String lowerCamelName(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static String unitName(String key, Class<?> clazz) {
        return key + clazz.getSimpleName();
    }

    public static String getUnitName(Object value) {
        Class<?> clazz = value.getClass();
        Unit unit = clazz.getAnnotation(Unit.class);
        if (unit != null) {
            return unit.value();
        }
        return clazz.getSimpleName();
    }

}
