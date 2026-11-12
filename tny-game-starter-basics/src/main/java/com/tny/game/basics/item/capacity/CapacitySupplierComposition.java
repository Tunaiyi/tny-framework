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
