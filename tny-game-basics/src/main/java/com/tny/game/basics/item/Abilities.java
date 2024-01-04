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
        holder.register(value);
    }

    public static <T extends Ability> T check(String key) {
        return holder.check(key, "获取 {} Ability 不存在", key);
    }

    public static <T extends Ability> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 Ability 不存在", id);
    }

    public static <T extends Ability> T of(int id) {
        return holder.of(id);
    }

    public static <T extends Ability> T of(String key) {
        return holder.of(key);
    }

    public static <T extends Ability> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends Ability> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends Ability> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<Ability> enumerator() {
        return holder;
    }

}
