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

import com.tny.game.basics.item.*;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class StorableCapablerItem<IM extends ItemModel> extends BaseCapablerItem<IM> implements CapacityObjectQuerierCapabler {

    private transient CapacityObjectStorer storer;

    private transient CapacitySupplierComposition composition;

    protected StorableCapablerItem() {
        super();
    }

    protected StorableCapablerItem(CapacitySupplierComposition composition) {
        this.composition = composition;
    }

    protected StorableCapablerItem(long playerId, IM model, CapacityObjectStorer storer, CapacitySupplierComposition composition) {
        super(playerId, model);
        this.storer = storer;
        this.composition = composition;
    }

    protected CapacityObjectStorer getStorer() {
        return storer;
    }

    protected void setStorer(CapacityObjectStorer visitor) {
        this.storer = visitor;
    }

    @Override
    public CapacityObjectQuerier querier() {
        return storer;
    }

    @Override
    protected void accept(CapacitySupplier supplier) {
        this.composition.accept(supplier);
        storer.saveCapabler(this.getId(), this.getModelId(), composition);
    }

    @Override
    protected void accept(Collection<? extends CapacitySupplier> suppliers) {
        this.composition.accept(suppliers);
        storer.saveCapabler(this.getId(), this.getModelId(), composition);
    }

    @Override
    protected void remove(CapacitySupplier supplier) {
        this.composition.remove(supplier);
        storer.saveCapabler(this.getId(), this.getModelId(), composition);
    }

    @Override
    protected void remove(Collection<CapacitySupplier> suppliers) {
        this.composition.remove(suppliers);
        storer.saveCapabler(this.getId(), this.getModelId(), composition);
    }

    @Override
    protected void clear() {
        this.composition.clear();
        storer.saveCapabler(this.getId(), this.getModelId(), composition);
    }

    @Override
    protected void invalid() {
        storer.deleteCapabler(this);
    }

    @Override
    protected void effect() {
        storer.saveCapabler(this.getId(), this.getModelId(), composition);
    }

}
