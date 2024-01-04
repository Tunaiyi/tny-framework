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
