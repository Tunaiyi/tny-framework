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

import com.tny.game.basics.item.*;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class CompositionCapablerItem<IM extends ItemModel> extends BaseCapablerItem<IM> implements CompositionCapabler {

    private final transient CapacitySupplierComposition composition;

    protected CompositionCapablerItem() {
        this(new DefaultCapacitySupplierComposition());
    }

    protected CompositionCapablerItem(CapacitySupplierComposition composition) {
        this.composition = composition;
    }

    protected CompositionCapablerItem(long playerId, IM model, CapacitySupplierComposition composition) {
        super(playerId, model);
        this.composition = composition;
    }

    @Override
    public CapableComposition composition() {
        return composition;
    }

    @Override
    protected void accept(CapacitySupplier supplier) {
        this.composition.accept(supplier);
    }

    @Override
    protected void accept(Collection<? extends CapacitySupplier> suppliers) {
        this.composition.accept(suppliers);
    }

    @Override
    protected void remove(CapacitySupplier supplier) {
        this.composition.remove(supplier);
    }

    @Override
    protected void remove(Collection<CapacitySupplier> suppliers) {
        this.composition.remove(suppliers);
    }

    @Override
    protected void clear() {
        this.composition.clear();
    }

}
