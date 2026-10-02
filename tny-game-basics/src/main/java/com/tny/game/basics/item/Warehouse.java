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

import java.util.function.BiFunction;

/**
 * 残酷系统
 *
 * @author KGTny
 */
public interface Warehouse extends Any {

    /**
     * 获取指定itemType的Storage对象
     *
     * @param itemType 事物类型
     * @return 返回storage对象
     */
    <O extends StuffOwner<?, ?>> O loadOwner(ItemType itemType, BiFunction<Warehouse, ItemType, O> ownerSupplier);

}
