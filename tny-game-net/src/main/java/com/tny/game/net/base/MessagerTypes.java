/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.base;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * 服务器工具栏
 * Created by Kun Yang on 16/1/27.
 */
public class MessagerTypes extends ClassImporter {

    private static final EnumerableSymbol<MessagerType, String> GROUP_SYMBOL = EnumerableSymbol.symbolOf(
            MessagerType.class, "group", MessagerType::getGroup);

    protected static EnumeratorHolder<MessagerType> holder = new EnumeratorHolder<MessagerType>() {

        @Override
        protected void postRegister(MessagerType object) {
            putAndCheckSymbol(GROUP_SYMBOL, object);
        }

    };

    private MessagerTypes() {
    }

    static void register(MessagerType value) {
        holder.register(value);
    }

    public static <T extends MessagerType> T check(String key) {
        return holder.check(key, "获取 {} MessagerType 不存在", key);
    }

    public static <T extends MessagerType> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 MessagerType 不存在", id);
    }

    public static <T extends MessagerType> T checkGroup(String group) {
        return holder.checkBySymbol(GROUP_SYMBOL, group, "获取 {} MessagerType 不存在", group);
    }

    public static <T extends MessagerType> T of(int id) {
        return holder.of(id);
    }

    public static <T extends MessagerType> T of(String key) {
        return holder.of(key);
    }

    public static <T extends MessagerType> T ofGroup(String key) {
        return holder.ofBySymbol(GROUP_SYMBOL, key);
    }

    public static <T extends MessagerType> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends MessagerType> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends MessagerType> Optional<T> optionGroup(String group) {
        return holder.optionBySymbol(GROUP_SYMBOL, group);
    }

    public static <T extends MessagerType> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<MessagerType> enumerator() {
        return holder;
    }

}
