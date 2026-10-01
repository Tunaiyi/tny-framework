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
public final class DemandTypes extends ClassImporter {

    protected static EnumeratorHolder<DemandType> holder = new EnumeratorHolder<>();

    //    static {
    //        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_DEMAND_TYPE_CLASS);
    //    }

    private DemandTypes() {
    }

    static void register(DemandType value) {
        EnumRegistrySupport.register(holder, value);
    }

    //    现状差异钉桩（禁止顺手修，见 verification-group6 遗留登记）：本类 check 双通道委托宽松 of 通道，
    //    未命中返回 null；兄弟门面 check 为严格通道（未命中抛 NPE）。收敛仅统一代码形态，不统一语义。
    public static <T extends DemandType> T check(String key) {
        return EnumRegistrySupport.of(holder, key);
    }

    public static <T extends DemandType> T check(int id) {
        return EnumRegistrySupport.of(holder, id);
    }

    public static <T extends DemandType> T of(int id) {
        return EnumRegistrySupport.of(holder, id);
    }

    public static <T extends DemandType> T of(String key) {
        return EnumRegistrySupport.of(holder, key);
    }

    public static <T extends DemandType> Optional<T> option(int id) {
        return EnumRegistrySupport.option(holder, id);
    }

    public static <T extends DemandType> Optional<T> option(String key) {
        return EnumRegistrySupport.option(holder, key);
    }

    public static <T extends DemandType> Collection<T> all() {
        return EnumRegistrySupport.all(holder);
    }

    public static Enumerator<DemandType> enumerator() {
        return EnumRegistrySupport.enumerator(holder);
    }

}
