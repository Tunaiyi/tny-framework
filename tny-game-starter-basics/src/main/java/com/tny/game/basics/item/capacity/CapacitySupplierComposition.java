/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.capacity;

import java.util.Collection;

public interface CapacitySupplierComposition extends CapableComposition {

    /**
     * 接受指定的 supplier
     *
     * @param supplier 指定的 supplier
     */
    default boolean accept(CapacitySupplier supplier) {
        return this.doAccept(supplier);
    }

    default boolean accept(Collection<? extends CapacitySupplier> suppliers) {
        boolean acc = false;
        for (CapacitySupplier supplier : suppliers) {
            if (this.doAccept(supplier)) {
                acc = true;
            }
        }
        return acc;
    }

    /**
     * 移除指定的 supplier
     *
     * @param supplier 指定的 supplier
     */
    default boolean remove(CapacitySupplier supplier) {
        return this.doRemove(supplier);
    }

    default boolean remove(Collection<? extends CapacitySupplier> suppliers) {
        return suppliers.stream().anyMatch(this::doRemove);
    }

    boolean doAccept(CapacitySupplier supplier);

    boolean doRemove(CapacitySupplier supplier);

    void clear();

}
