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
public abstract class BaseCapablerItem<IM extends ItemModel> extends BaseItem<IM> implements Capabler {

    protected BaseCapablerItem() {
    }

    protected BaseCapablerItem(long playerId, IM model) {
        super(playerId, model);
    }

    protected abstract void accept(CapacitySupplier supplier);

    protected abstract void accept(Collection<? extends CapacitySupplier> suppliers);

    protected abstract void remove(CapacitySupplier supplier);

    protected abstract void remove(Collection<CapacitySupplier> suppliers);

    protected abstract void clear();

    protected abstract void invalid();

    protected abstract void effect();

}
