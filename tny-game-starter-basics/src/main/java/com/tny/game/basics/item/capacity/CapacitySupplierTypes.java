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

package com.tny.game.basics.item.capacity;

import com.tny.game.common.enums.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/4/4.
 */
public class CapacitySupplierTypes {

    protected static EnumeratorHolder<CapacitySupplierType> holder = new EnumeratorHolder<>();

    private CapacitySupplierTypes() {
    }

    static void register(CapacitySupplierType value) {
        holder.register(value);
    }

    public static <T extends CapacitySupplierType> T of(String key) {
        return holder.check(key, "获取 {} CapacitySupplierType 不存在", key);
    }

    public static <T extends CapacitySupplierType> T of(int id) {
        return holder.check(id, "获取 ID为 {} 的 CapacitySupplierType 不存在", id);
    }

    public static Collection<CapacitySupplierType> getAll() {
        return holder.allValues();
    }

}
