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

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class CapacitySupplierItem<IM extends CapacitySupplierItemModel> extends BaseItem<IM> implements CapacitySupplier {

    protected CapacitySupplierItem() {
    }

    protected CapacitySupplierItem(long playerId, IM model) {
        super(playerId, model);
    }

    @Override
    public CapacitySupplierType getSupplierType() {
        return model.getSupplierType();
    }

    protected abstract void refresh();

    protected abstract void invalid();

    protected abstract void effect();

}
