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
