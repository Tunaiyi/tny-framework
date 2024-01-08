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

package com.tny.game.basics.item.capacity.event;

import com.tny.game.basics.item.capacity.*;
import com.tny.game.common.event.bus.*;

/**
 * Created by Kun Yang on 16/2/17.
 */
public interface CapacityEvents {

    A1BindEvent<CapacityListener, CapacitySupply, CapacitySupplier>
            ON_CHANGE = Events.ofEvent(CapacityListener.class, CapacityListener::onChange);

    A1BindEvent<CapacityListener, CapacitySupply, CapacitySupplier>
            ON_INVALID = Events.ofEvent(CapacityListener.class, CapacityListener::onInvalid);

    A1BindEvent<CapacityListener, CapacitySupply, CapacitySupplier>
            ON_EFFECT = Events.ofEvent(CapacityListener.class, CapacityListener::onEffect);

}
