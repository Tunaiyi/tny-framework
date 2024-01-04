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

package com.tny.game.net.endpoint;

import com.tny.game.common.event.bus.*;
import com.tny.game.net.application.*;
import com.tny.game.net.endpoint.listener.*;

/**
 * <p>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EndpointEventBuses extends BaseEventBuses<EndpointListener> {

    private final static EndpointEventBuses eventBuses = new EndpointEventBuses();

    private final BindVoidEventBus<EndpointOnlineListener, Endpoint> ON_ONLINE =
            EventBuses.of(EndpointOnlineListener.class, EndpointOnlineListener::onOnline);

    private final BindVoidEventBus<EndpointOfflineListener, Endpoint> ON_OFFLINE =
            EventBuses.of(EndpointOfflineListener.class, EndpointOfflineListener::onOffline);

    private final BindVoidEventBus<EndpointCloseListener, Endpoint> ON_CLOSE =
            EventBuses.of(EndpointCloseListener.class, EndpointCloseListener::onClose);


    private EndpointEventBuses() {
        super();
    }

    public static EndpointEventBuses buses() {
        return eventBuses;
    }

    public BindVoidEventBus<EndpointOnlineListener, Endpoint> onlineEvent() {
        return this.ON_ONLINE;
    }

    public BindVoidEventBus<EndpointOfflineListener, Endpoint> offlineEvent() {
        return this.ON_OFFLINE;
    }

    public BindVoidEventBus<EndpointCloseListener, Endpoint> closeEvent() {
        return this.ON_CLOSE;
    }

}
