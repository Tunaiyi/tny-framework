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

package com.tny.game.basics.item;

import com.tny.game.basics.utlis.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Abilities extends ClassImporter {

    private static EnumeratorHolder<Ability> holder = new EnumeratorHolder<>();

    //    static {
    //        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_ABILITY_CLASS);
    //    }

    private Abilities() {
    }

    static void register(Ability value) {
        EnumRegistrySupport.register(holder, value);
    }

    public static <T extends Ability> T check(String key) {
        return EnumRegistrySupport.check(holder, key, "Ability");
    }

    public static <T extends Ability> T check(int id) {
        return EnumRegistrySupport.check(holder, id, "Ability");
    }

    public static <T extends Ability> T of(int id) {
        return EnumRegistrySupport.of(holder, id);
    }

    public static <T extends Ability> T of(String key) {
        return EnumRegistrySupport.of(holder, key);
    }

    public static <T extends Ability> Optional<T> option(int id) {
        return EnumRegistrySupport.option(holder, id);
    }

    public static <T extends Ability> Optional<T> option(String key) {
        return EnumRegistrySupport.option(holder, key);
    }

    public static <T extends Ability> Collection<T> all() {
        return EnumRegistrySupport.all(holder);
    }

    public static Enumerator<Ability> enumerator() {
        return EnumRegistrySupport.enumerator(holder);
    }

}
