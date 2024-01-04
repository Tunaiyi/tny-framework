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

package com.tny.game.basics.item.capacity.event;

import com.tny.game.basics.item.capacity.*;

/**
 * Created by Kun Yang on 16/2/17.
 */
public interface CapacityListener {

    default void onChange(CapacitySupply source, CapacitySupplier owner) {
    }

    default void onInvalid(CapacitySupply source, CapacitySupplier owner) {
    }

    default void onEffect(CapacitySupply source, CapacitySupplier owner) {
    }

}
