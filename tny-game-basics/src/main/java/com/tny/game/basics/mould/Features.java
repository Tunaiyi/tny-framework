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

package com.tny.game.basics.mould;

import com.tny.game.basics.utlis.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Features extends ClassImporter {

    private static final EnumeratorHolder<Feature> holder = new EnumeratorHolder<>();

    //    static {
    //        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_FEATURE_CLASS);
    //    }

    private Features() {
    }

    //    可见性现状保留（D5）：本类 register 为 public 装载入口（兄弟门面为包私有）
    public static void register(Feature value) {
        EnumRegistrySupport.register(holder, value);
    }

    public static <T extends Feature> T check(String key) {
        return EnumRegistrySupport.check(holder, key, "Feature");
    }

    public static <T extends Feature> T check(int id) {
        return EnumRegistrySupport.check(holder, id, "Feature");
    }

    public static <T extends Feature> T of(int id) {
        return EnumRegistrySupport.of(holder, id);
    }

    public static <T extends Feature> T of(String key) {
        return EnumRegistrySupport.of(holder, key);
    }

    public static <T extends Feature> Optional<T> option(int id) {
        return EnumRegistrySupport.option(holder, id);
    }

    public static <T extends Feature> Optional<T> option(String key) {
        return EnumRegistrySupport.option(holder, key);
    }

    public static <T extends Feature> Collection<T> all() {
        return EnumRegistrySupport.all(holder);
    }

    public static Enumerator<Feature> enumerator() {
        return EnumRegistrySupport.enumerator(holder);
    }

}
