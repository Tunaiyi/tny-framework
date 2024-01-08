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
package com.tny.game.net.session.listener;

import com.tny.game.common.event.bus.*;
import com.tny.game.net.application.*;
import com.tny.game.net.session.TunnelConnector;
import com.tny.game.net.transport.*;

import java.util.stream.Stream;

/**
 * <p>
 */
public class ClientEventBuses extends EventWatches<ClientListener> {

    private static final A1BindEvent<ClientActivateListener, TunnelConnector, Tunnel> ACTIVATE_EVENT =
            Events.ofEvent(ClientActivateListener.class, ClientActivateListener::onActivate);

    private static final A1BindEvent<ClientUnactivatedListener, TunnelConnector, Tunnel> UNACTIVATED_EVENT =
            Events.ofEvent(ClientUnactivatedListener.class, ClientUnactivatedListener::onUnactivated);

    private static final VoidBindEvent<ClientOpenListener, TunnelConnector> OPEN_EVENT =
            Events.ofEvent(ClientOpenListener.class, ClientOpenListener::onOpen);

    private static final VoidBindEvent<ClientCloseListener, TunnelConnector> CLOSE_EVENT =
            Events.ofEvent(ClientCloseListener.class, ClientCloseListener::onClose);

    private final A1BindEvent<ClientActivateListener, TunnelConnector, Tunnel> activateEvent;
    private final A1BindEvent<ClientUnactivatedListener, TunnelConnector, Tunnel> unactivatedEvent;
    private final VoidBindEvent<ClientOpenListener, TunnelConnector> openEvent;
    private final VoidBindEvent<ClientCloseListener, TunnelConnector> closeEvent;

    private ClientEventBuses() {
        activateEvent = ACTIVATE_EVENT.forkChild();
        unactivatedEvent = UNACTIVATED_EVENT.forkChild();
        openEvent = OPEN_EVENT.forkChild();
        closeEvent = CLOSE_EVENT.forkChild();
    }

    @Override
    protected Stream<EventListen<? extends ClientListener>> eventStream() {
        return Stream.of(activateEvent, unactivatedEvent, openEvent, closeEvent);
    }

    public A1BindEvent<ClientActivateListener, TunnelConnector, Tunnel> globalActivateEvent() {
        return ACTIVATE_EVENT;
    }

    public A1BindEvent<ClientUnactivatedListener, TunnelConnector, Tunnel> globalUnactivatedEvent() {
        return UNACTIVATED_EVENT;
    }

    public VoidBindEvent<ClientOpenListener, TunnelConnector> globalOpenEvent() {
        return OPEN_EVENT;
    }

    public VoidBindEvent<ClientCloseListener, TunnelConnector> globalCloseEvent() {
        return CLOSE_EVENT;
    }

    A1BindEvent<ClientActivateListener, TunnelConnector, Tunnel> activateEvent() {
        return activateEvent;
    }

    A1BindEvent<ClientUnactivatedListener, TunnelConnector, Tunnel> unactivatedEvent() {
        return unactivatedEvent;
    }

    VoidBindEvent<ClientOpenListener, TunnelConnector> openEvent() {
        return openEvent;
    }

    VoidBindEvent<ClientCloseListener, TunnelConnector> closeEvent() {
        return closeEvent;
    }

    public EventListen<ClientActivateListener> activateWatch() {
        return activateEvent;
    }

    public EventListen<ClientUnactivatedListener> unactivatedWatch() {
        return unactivatedEvent;
    }

    public EventListen<ClientOpenListener> openWatch() {
        return openEvent;
    }

    public EventListen<ClientCloseListener> closeWatch() {
        return closeEvent;
    }
}
