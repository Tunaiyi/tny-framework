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
