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
public abstract class ContainerCapacitySupplierItem<IM extends CapacitySupplierItemModel>
        extends CapacitySupplierItem<IM> implements ContainerCapacitySupplier {

    private transient CapacityContainer container;

    protected ContainerCapacitySupplierItem() {
    }

    protected ContainerCapacitySupplierItem(long playerId, IM model) {
        super(playerId, model);
    }

    @Override
    protected void setModel(IM model) {
        super.setModel(model);
        this.initContainer();
    }

    private void initContainer() {
        if (this.container == null) {
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
    public CapacitySupply supply() {
        return container();
    }

    @Override
    protected void refresh() {
        this.container().refresh(this);
    }

    @Override
    protected void invalid() {
        this.container().refresh(this);
    }

    @Override
    protected void effect() {
        this.container().effect(this);
    }

    @Override
    public int getModelId() {
        return super.getModelId();
    }

    @Override
    public long getPlayerId() {
        return super.getPlayerId();
    }

}
