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

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class BaseCapacitySupplierComposition implements CapacitySupplierComposition {

    protected volatile Set<CapacitySupplier> suppliers = new CopyOnWriteArraySet<>();

    @Override
    public Collection<? extends CapacitySupplier> suppliers() {
        return Collections.unmodifiableCollection(suppliers);
    }

    @Override
    public boolean doAccept(CapacitySupplier supplier) {
        this.suppliers.remove(supplier);
        return suppliers.add(supplier);
    }

    @Override
    public boolean doRemove(CapacitySupplier supplier) {
        return suppliers.remove(supplier);
    }

    @Override
    public void clear() {
        this.suppliers = new CopyOnWriteArraySet<>();
    }

    @Override
    public void collectCapacities(CapacityCollector collector, Collection<? extends Capacity> capacities) {
        suppliers.forEach(supplier -> supplier.collectCapacities(collector, capacities));
    }

}
