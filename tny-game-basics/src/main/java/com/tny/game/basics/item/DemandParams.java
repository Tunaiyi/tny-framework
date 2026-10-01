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
public final class DemandParams extends ClassImporter {

    protected static EnumeratorHolder<DemandParam> holder = new EnumeratorHolder<>();

    private DemandParams() {
    }

    static void register(DemandParam value) {
        EnumRegistrySupport.register(holder, value);
    }

    public static <T extends DemandParam> T check(String key) {
        return EnumRegistrySupport.check(holder, key, "DemandParam");
    }

    public static <T extends DemandParam> T check(int id) {
        return EnumRegistrySupport.check(holder, id, "DemandParam");
    }

    public static <T extends DemandParam> T of(int id) {
        return EnumRegistrySupport.of(holder, id);
    }

    public static <T extends DemandParam> T of(String key) {
        return EnumRegistrySupport.of(holder, key);
    }

    public static <T extends DemandParam> Optional<T> option(int id) {
        return EnumRegistrySupport.option(holder, id);
    }

    public static <T extends DemandParam> Optional<T> option(String key) {
        return EnumRegistrySupport.option(holder, key);
    }

    public static <T extends DemandParam> Collection<T> all() {
        return EnumRegistrySupport.all(holder);
    }

    public static Enumerator<DemandParam> enumerator() {
        return EnumRegistrySupport.enumerator(holder);
    }

}
