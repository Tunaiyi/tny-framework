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
package com.tny.game.net.transport;

import com.tny.game.common.event.*;
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
public class TunnelEvents extends EventWatches<TunnelListener> implements TunnelEventWatches{

    private static final VoidBindEvent<TunnelActivateListener, Tunnel> ACTIVATE_EVENT =
            Events.ofEvent(TunnelActivateListener.class, TunnelActivateListener::onActivate);

    private static final VoidBindEvent<TunnelUnactivatedListener, Tunnel> UNACTIVATED_EVENT =
            Events.ofEvent(TunnelUnactivatedListener.class, TunnelUnactivatedListener::onUnactivated);

    private static final VoidBindEvent<TunnelCloseListener, Tunnel> CLOSE_EVENT =
            Events.ofEvent(TunnelCloseListener.class, TunnelCloseListener::onClose);

    public static VoidBindEvent<TunnelActivateListener, Tunnel> globalActivateEvent() {
        return ACTIVATE_EVENT;
    }

    public static VoidBindEvent<TunnelUnactivatedListener, Tunnel> globalUnactivatedEvent() {
        return UNACTIVATED_EVENT;
    }

    public static VoidBindEvent<TunnelCloseListener, Tunnel> globalCloseEvent() {
        return CLOSE_EVENT;
    }

    private final VoidBindEvent<TunnelActivateListener, Tunnel> activateEvent;

    private final VoidBindEvent<TunnelUnactivatedListener, Tunnel> unactivatedEvent;

    private final VoidBindEvent<TunnelCloseListener, Tunnel> closeEvent;


    public VoidBindEvent<TunnelActivateListener, Tunnel> ActivateEvent() {
        return activateEvent;
    }

    public VoidBindEvent<TunnelUnactivatedListener, Tunnel> UnactivatedEvent() {
        return unactivatedEvent;
    }

    public VoidBindEvent<TunnelCloseListener, Tunnel> CloseEvent() {
        return closeEvent;
    }

    protected TunnelEvents() {
        activateEvent = ACTIVATE_EVENT.forkChild();
        unactivatedEvent = UNACTIVATED_EVENT.forkChild();
        closeEvent = CLOSE_EVENT.forkChild();
    }

    @Override
    protected Stream<EventListen<? extends TunnelListener>> eventStream() {
        return Stream.of(activateEvent, unactivatedEvent, closeEvent);
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

}
