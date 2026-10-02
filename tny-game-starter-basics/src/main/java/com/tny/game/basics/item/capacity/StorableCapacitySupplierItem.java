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

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class StorableCapacitySupplierItem<IM extends CapacitySupplierItemModel>
        extends CapacitySupplierItem<IM> implements CapacityObjectQuerierSupplier {

    private transient CapacityObjectStorer storer;

    private transient CapacityContainer container;

    protected StorableCapacitySupplierItem(CapacityObjectStorer storer) {
        this.storer = storer;
    }

    protected StorableCapacitySupplierItem(long playerId, IM model, CapacityObjectStorer storer) {
        super(playerId, model);
        this.storer = storer;
    }

    @Override
    protected void setModel(IM model) {
        super.setModel(model);
        this.initContainer();
    }

    private void initContainer() {
        if (this.container != null) {
            this.container = createContainer();
        }
    }

    protected abstract CapacityContainer createContainer();

    private CapacityContainer container() {
        if (this.container == null) {
            initContainer();
        }
        return this.container;
    }

    @Override
    public CapacityObjectQuerier querier() {
        return storer;
    }

    protected void setStorer(CapacityObjectStorer storer) {
        this.storer = storer;
    }

    @Override
    protected void refresh() {
        this.storer.saveSupplier(this.getSupplierType(), this.getId(), this.getModelId(), this.container());
    }

    @Override
    protected void invalid() {
        this.storer.deleteSupplier(this);
    }

    @Override
    protected void effect() {
        this.storer.saveSupplier(this.getSupplierType(), this.getId(), this.getModelId(), this.container());
    }

}
