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

package com.tny.game.basics.item;

import com.tny.game.basics.log.*;
import com.tny.game.common.concurrent.collection.*;
import org.slf4j.*;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Map;
import java.util.function.BiFunction;

import static com.tny.game.common.utils.ObjectAide.*;

public class GameWarehouse implements Warehouse {

    protected final static Logger LOGGER = LoggerFactory.getLogger(LogName.WAREHOUSE);

    protected long playerId;

    private final Map<ItemType, WeakReference<StuffOwner<?, ?>>> stuffOwnerMap = new CopyOnWriteMap<>();

    public GameWarehouse(long playerId) {
        this.playerId = playerId;
    }

    @Override
    public long getId() {
        return this.playerId;
    }

    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    @Override
    public <O extends StuffOwner<?, ?>> O loadOwner(ItemType itemType, BiFunction<Warehouse, ItemType, O> ownerSupplier) {
        WeakReference<StuffOwner<?, ?>> reference = this.stuffOwnerMap.get(itemType);
        StuffOwner<?, ?> owner = reference != null ? reference.get() : null;
        if (owner != null) {
            return as(owner);
        }
        owner = ownerSupplier.apply(this, itemType);
        if (owner == null) {
            throw new NullPointerException(MessageFormat.format("{0} 玩家 {1} {2} owner的对象为 null", this.playerId, itemType));
        }
        reference = new WeakReference<>(owner);
        this.stuffOwnerMap.put(itemType, reference);
        return as(owner);
    }

}
