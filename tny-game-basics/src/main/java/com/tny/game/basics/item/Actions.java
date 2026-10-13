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

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.utlis.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Actions extends ClassImporter {

    protected static EnumeratorHolder<Action> holder = new EnumeratorHolder<>();

    //    static {
    //        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_ACTION_CLASS);
    //    }

    private Actions() {
    }

    static void register(Action value) {
        EnumRegistrySupport.register(holder, value);
    }

    public static <T extends Action> T check(String key) {
        return EnumRegistrySupport.check(holder, key, "Action");
    }

    public static <T extends Action> T check(int id) {
        return EnumRegistrySupport.check(holder, id, "Action");
    }

    public static <T extends Action> T of(int id) {
        return EnumRegistrySupport.of(holder, id);
    }

    public static <T extends Action> T of(String key) {
        return EnumRegistrySupport.of(holder, key);
    }

    public static <T extends Action> Optional<T> option(int id) {
        return EnumRegistrySupport.option(holder, id);
    }

    public static <T extends Action> Optional<T> option(String key) {
        return EnumRegistrySupport.option(holder, key);
    }

    //    异名薄委托保留（D5）：兄弟门面为 all()，本类历史名为 getAll()，不得借收敛改名
    public static Collection<Action> getAll() {
        return EnumRegistrySupport.all(holder);
    }

    public static Enumerator<Action> enumerator() {
        return EnumRegistrySupport.enumerator(holder);
    }

}
