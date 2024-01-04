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

package com.tny.game.net.application;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class AppTypes extends ClassImporter {

    private static final EnumerableSymbol<AppType, String> APP_NAME_SYMBOL = EnumerableSymbol.symbolOf(
            AppType.class, "appName", AppType::getAppName);

    private static EnumeratorHolder<AppType> holder = new EnumeratorHolder<AppType>() {

        @Override
        protected void postRegister(AppType object) {
            putAndCheckSymbol(APP_NAME_SYMBOL, object);
        }

    };

    private AppTypes() {
    }

    static void register(AppType value) {
        holder.register(value);
    }

    public static <T extends AppType> T of(String name) {
        return holder.check(name, "获取 {} AppType 不存在", name);
    }

    public static <T extends AppType> T ofAppName(String name) {
        return holder.checkBySymbol(APP_NAME_SYMBOL, name, "获取 {} AppType 不存在", name);
    }

    public static <T extends AppType> T valueOfEnum(String enumName) {
        return holder.check(enumName, "获取 {} AppType 不存在", enumName);
    }

    public static Collection<AppType> getAll() {
        return holder.allValues();
    }

}
