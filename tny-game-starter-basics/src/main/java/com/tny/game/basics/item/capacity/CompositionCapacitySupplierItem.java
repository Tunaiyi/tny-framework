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

public abstract class CompositionCapacitySupplierItem<IM extends CapacitySupplierItemModel>
        extends CapacitySupplierItem<IM> implements CompositionCapacitySupplier {

    private final transient CapacitySupplierComposition composition;

    protected CompositionCapacitySupplierItem() {
        this(new DefaultCapacitySupplierComposition());
    }

    protected CompositionCapacitySupplierItem(CapacitySupplierComposition composition) {
        this.composition = composition;
    }

    protected CompositionCapacitySupplierItem(long playerId, IM model) {
        this(playerId, model, new DefaultCapacitySupplierComposition());
    }

    protected CompositionCapacitySupplierItem(long playerId, IM model, CapacitySupplierComposition composition) {
        super(playerId, model);
        this.composition = composition;
    }

    @Override
    public CapacitySupplierComposition composition() {
        return composition;
    }

    protected void accept(CapacitySupplier supplier) {
        this.composition.accept(supplier);
    }

    protected void accept(Collection<? extends CapacitySupplier> suppliers) {
        this.composition.accept(suppliers);
    }

    protected void remove(CapacitySupplier supplier) {
        this.composition.remove(supplier);
    }

    protected void remove(Collection<CapacitySupplier> suppliers) {
        this.composition.remove(suppliers);
    }

    protected void clear() {
        this.composition.clear();
    }

    @Override
    protected void refresh() {

    }

    @Override
    protected void invalid() {

    }

    @Override
    protected void effect() {

    }

}
