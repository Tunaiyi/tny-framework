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

package com.tny.game.net.session;

import com.tny.game.common.event.*;
import com.tny.game.net.application.*;
import com.tny.game.net.session.listener.*;

import java.util.stream.Stream;

/**
 * <p>
 */
public class SessionEvents extends EventWatches<SessionListener> implements SessionEventWatches {

    private static final VoidBindEvent<SessionOnlineListener, Session> ONLINE_EVENT = Events.ofEvent(SessionOnlineListener.class,
            SessionOnlineListener::onOnline);

    private static final VoidBindEvent<SessionOfflineListener, Session> OFFLINE_EVENT = Events.ofEvent(SessionOfflineListener.class,
            SessionOfflineListener::onOffline);

    private static final VoidBindEvent<SessionCloseListener, Session> CLOSE_EVENT = Events.ofEvent(SessionCloseListener.class,
            SessionCloseListener::onClose);

    public static EventListen<SessionOnlineListener> globalOnlineWatch() {
        return ONLINE_EVENT;
    }

    public static EventListen<SessionOfflineListener> globalOfflineWatch() {
        return OFFLINE_EVENT;
    }

    public static EventListen<SessionCloseListener> globalCloseWatch() {
        return CLOSE_EVENT;
    }

    private final VoidBindEvent<SessionOnlineListener, Session> onlineEvent;

    private final VoidBindEvent<SessionOfflineListener, Session> offlineEvent;

    private final VoidBindEvent<SessionCloseListener, Session> closeEvent;


    public SessionEvents() {
        this.onlineEvent = ONLINE_EVENT.forkChild();
        this.offlineEvent = OFFLINE_EVENT.forkChild();
        this.closeEvent = CLOSE_EVENT.forkChild();
    }

    @Override
    public Stream<EventListen<? extends SessionListener>> eventStream() {
        return Stream.of(onlineEvent, offlineEvent, closeEvent);
    }


    VoidBindEvent<SessionOnlineListener, Session> onlineEvent() {
        return this.onlineEvent;
    }

    VoidBindEvent<SessionOfflineListener, Session> offlineEvent() {
        return this.offlineEvent;
    }

    VoidBindEvent<SessionCloseListener, Session> closeEvent() {
        return this.closeEvent;
    }

    @Override
    public EventListen<SessionOnlineListener> onlineWatch() {
        return onlineEvent();
    }

    @Override
    public EventListen<SessionOfflineListener> offlineWatch() {
        return offlineEvent();
    }

    @Override
    public EventListen<SessionCloseListener> closeWatch() {
        return closeEvent();
    }

}
