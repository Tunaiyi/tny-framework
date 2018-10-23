package com.tny.game.net.session;

import com.tny.game.common.event.*;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.transport.listener.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-03 15:55
 */
public class SessionEvents {

    @SuppressWarnings("unchecked")
    protected static final BindP1EventBus<SessionAcceptListener, Session, Tunnel> ON_ACCEPT =
            EventBuses.of(SessionAcceptListener.class, SessionAcceptListener::onAccept);

    @SuppressWarnings("unchecked")
    protected static final BindVoidEventBus<SessionOnlineListener, Session> ON_ONLINE =
            EventBuses.of(SessionOnlineListener.class, SessionOnlineListener::onOnline);

    @SuppressWarnings("unchecked")
    protected static final BindVoidEventBus<SessionOfflineListener, Session> ON_OFFLINE =
            EventBuses.of(SessionOfflineListener.class, SessionOfflineListener::onOffline);

    @SuppressWarnings("unchecked")
    protected static final BindVoidEventBus<SessionCloseListener, Session> ON_CLOSE =
            EventBuses.of(SessionCloseListener.class, SessionCloseListener::onClose);

    private SessionEvents() {
    }

    public static ListenerRegister<SessionAcceptListener> acceptEventBus() {
        return ON_ACCEPT;
    }

    public static ListenerRegister<SessionOnlineListener> onlineEventBus() {
        return ON_ONLINE;
    }

    public static ListenerRegister<SessionOfflineListener> offlineEventBus() {
        return ON_OFFLINE;
    }

    public static ListenerRegister<SessionCloseListener> closeEventBus() {
        return ON_CLOSE;
    }

}
