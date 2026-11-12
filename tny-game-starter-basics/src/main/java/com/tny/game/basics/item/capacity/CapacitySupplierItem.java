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
