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
