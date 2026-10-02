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

package com.tny.game.boot.utils;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/18 2:33 下午
 */
public final class BeanNameUtils {

    private static final String DEFAULT_HEAD = "default";

    private BeanNameUtils() {
    }

    public static String defaultName(Class<?> clazz) {
        return DEFAULT_HEAD + clazz.getSimpleName();
    }

    public static String lowerCamelName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static String nameOf(String namePrefix, Class<?> clazz) {
        return namePrefix + clazz.getSimpleName();
    }

    public static String lowerCamelName(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static String upperCamelName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String upperCamelName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String unitName(String key, Class<?> clazz) {
        return key + clazz.getSimpleName();
    }

}
