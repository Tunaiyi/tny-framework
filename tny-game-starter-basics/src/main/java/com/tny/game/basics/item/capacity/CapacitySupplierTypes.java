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
