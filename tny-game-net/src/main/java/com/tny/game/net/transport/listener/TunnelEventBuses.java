/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.transport.listener;

import com.tny.game.common.event.bus.*;
import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-03 15:55
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TunnelEventBuses extends BaseEventBuses<TunnelListener> {

    // @SuppressWarnings("unchecked")
    // private final BindVoidEventBus<TunnelAuthenticateListener, Tunnel> ON_AUTHENTICATE =
    //         EventBuses.of(TunnelAuthenticateListener.class, TunnelAuthenticateListener::onAuthenticate);

    private final BindVoidEventBus<TunnelActivateListener, Tunnel> ON_ACTIVATE =
            EventBuses.of(TunnelActivateListener.class, TunnelActivateListener::onActivate);

    private final BindVoidEventBus<TunnelUnactivatedListener, Tunnel> ON_UNACTIVATED =
            EventBuses.of(TunnelUnactivatedListener.class, TunnelUnactivatedListener::onUnactivated);

    private final BindVoidEventBus<TunnelCloseListener, Tunnel> ON_CLOSE =
            EventBuses.of(TunnelCloseListener.class, TunnelCloseListener::onClose);

    private final static TunnelEventBuses eventBuses = new TunnelEventBuses();

    private TunnelEventBuses() {
        super();
    }

    public static TunnelEventBuses buses() {
        return eventBuses;
    }

    public BindVoidEventBus<TunnelActivateListener, Tunnel> activateEvent() {
        return this.ON_ACTIVATE;
    }

    public BindVoidEventBus<TunnelUnactivatedListener, Tunnel> unactivatedEvent() {
        return this.ON_UNACTIVATED;
    }

    public BindVoidEventBus<TunnelCloseListener, Tunnel> closeEvent() {
        return this.ON_CLOSE;
    }

}
