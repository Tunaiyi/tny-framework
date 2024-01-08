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
package com.tny.game.net.transport;

import com.tny.game.common.event.bus.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.transport.listener.*;

import java.util.stream.Stream;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-09-03 15:55
 */
public class TunnelEventBuses extends EventWatches<TunnelListener> implements TunnelEventWatches{

    private static final VoidBindEvent<TunnelActivateListener, Tunnel> ACTIVATE_EVENT =
            Events.ofEvent(TunnelActivateListener.class, TunnelActivateListener::onActivate);

    private static final VoidBindEvent<TunnelUnactivatedListener, Tunnel> UNACTIVATED_EVENT =
            Events.ofEvent(TunnelUnactivatedListener.class, TunnelUnactivatedListener::onUnactivated);

    private static final VoidBindEvent<TunnelCloseListener, Tunnel> CLOSE_EVENT =
            Events.ofEvent(TunnelCloseListener.class, TunnelCloseListener::onClose);

    private static final A1BindEvent<TunnelReceiveListener, Tunnel, RpcEnterContext> RECEIVE_EVENT =
            Events.ofEvent(TunnelReceiveListener.class, TunnelReceiveListener::onReceive);

    public static VoidBindEvent<TunnelActivateListener, Tunnel> globalActivateEvent() {
        return ACTIVATE_EVENT;
    }

    public static VoidBindEvent<TunnelUnactivatedListener, Tunnel> globalUnactivatedEvent() {
        return UNACTIVATED_EVENT;
    }

    public static VoidBindEvent<TunnelCloseListener, Tunnel> globalCloseEvent() {
        return CLOSE_EVENT;
    }

    public static A1BindEvent<TunnelReceiveListener, Tunnel, RpcEnterContext> globalReceiveEvent() {
        return RECEIVE_EVENT;
    }

    private final VoidBindEvent<TunnelActivateListener, Tunnel> activateEvent;

    private final VoidBindEvent<TunnelUnactivatedListener, Tunnel> unactivatedEvent;

    private final VoidBindEvent<TunnelCloseListener, Tunnel> closeEvent;

    private final A1BindEvent<TunnelReceiveListener, Tunnel, RpcEnterContext> receiveEvent;


    public VoidBindEvent<TunnelActivateListener, Tunnel> ActivateEvent() {
        return activateEvent;
    }

    public VoidBindEvent<TunnelUnactivatedListener, Tunnel> UnactivatedEvent() {
        return unactivatedEvent;
    }

    public VoidBindEvent<TunnelCloseListener, Tunnel> CloseEvent() {
        return closeEvent;
    }

    public A1BindEvent<TunnelReceiveListener, Tunnel, RpcEnterContext> ReceiveEvent() {
        return receiveEvent;
    }

    protected TunnelEventBuses() {
        activateEvent = ACTIVATE_EVENT.forkChild();
        unactivatedEvent = UNACTIVATED_EVENT.forkChild();
        closeEvent = CLOSE_EVENT.forkChild();
        receiveEvent = RECEIVE_EVENT.forkChild();
    }

    @Override
    protected Stream<EventListen<? extends TunnelListener>> eventStream() {
        return Stream.of(activateEvent, unactivatedEvent, receiveEvent, closeEvent);
    }

    VoidBindEvent<TunnelActivateListener, Tunnel> activateEvent() {
        return this.activateEvent;
    }

    VoidBindEvent<TunnelUnactivatedListener, Tunnel> unactivatedEvent() {
        return this.unactivatedEvent;
    }

    VoidBindEvent<TunnelCloseListener, Tunnel> closeEvent() {
        return this.closeEvent;
    }

    A1BindEvent<TunnelReceiveListener, Tunnel, RpcEnterContext> receiveEvent() {
        return this.receiveEvent;
    }

    @Override
    public EventListen<TunnelActivateListener> activateWatch() {
        return activateEvent;
    }

    @Override
    public EventListen<TunnelUnactivatedListener> unactivatedWatch() {
        return unactivatedEvent;
    }

    @Override
    public EventListen<TunnelCloseListener> closeWatch() {
        return closeEvent;
    }

    @Override
    public EventListen<TunnelReceiveListener> receiveWatch() {
        return receiveEvent;
    }
}
