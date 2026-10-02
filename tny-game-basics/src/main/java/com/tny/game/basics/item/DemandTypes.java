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

    //    严格校验通道（fix-registered-defects tasks 6.1 转正，design D6/差量 enumeration-facade-semantics
    //    「严格校验通道不得降级为宽松空返」）：原委托宽松 of 通道的现状差异在册禁修期结束，
    //    接入兄弟门面同源的 EnumRegistrySupport.check 严格通道——未注册身份显式失败且携带身份信息。
    public static <T extends DemandType> T check(String key) {
        return EnumRegistrySupport.check(holder, key, "DemandType");
    }

    public static <T extends DemandType> T check(int id) {
        return EnumRegistrySupport.check(holder, id, "DemandType");
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
