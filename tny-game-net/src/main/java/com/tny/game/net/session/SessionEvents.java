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
