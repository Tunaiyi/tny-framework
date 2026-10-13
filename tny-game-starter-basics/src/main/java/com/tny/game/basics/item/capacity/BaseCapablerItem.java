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
