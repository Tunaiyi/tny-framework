package com.tny.game.net.endpoint.listener;

import com.tny.game.common.event.*;
import com.tny.game.net.base.BaseEventBuses;
import com.tny.game.net.endpoint.Session;
import com.tny.game.net.transport.Tunnel;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-06 10:24
 */
public class SessionEventBuses extends BaseEventBuses<SessionListener> {

    @SuppressWarnings("unchecked")
    private final BindP1EventBus<SessionAcceptListener, Session, Tunnel> ON_ACCEPT =
            EventBuses.of(SessionAcceptListener.class, SessionAcceptListener::onAccept);

    @SuppressWarnings("unchecked")
    private final BindVoidEventBus<SessionOnlineListener, Session> ON_ONLINE =
            EventBuses.of(SessionOnlineListener.class, SessionOnlineListener::onOnline);

    @SuppressWarnings("unchecked")
    private final BindVoidEventBus<SessionOfflineListener, Session> ON_OFFLINE =
            EventBuses.of(SessionOfflineListener.class, SessionOfflineListener::onOffline);

    @SuppressWarnings("unchecked")
    private final BindVoidEventBus<SessionCloseListener, Session> ON_CLOSE =
            EventBuses.of(SessionCloseListener.class, SessionCloseListener::onClose);

    private final static SessionEventBuses eventBuses = new SessionEventBuses();

    private SessionEventBuses() {
        super();
    }

    public static SessionEventBuses buses() {
        return eventBuses;
    }

    public BindP1EventBus<SessionAcceptListener, Session, Tunnel> acceptEvent() {
        return ON_ACCEPT;
    }

    public BindVoidEventBus<SessionOnlineListener, Session> onlineEvent() {
        return ON_ONLINE;
    }

    public BindVoidEventBus<SessionOfflineListener, Session> offlineEvent() {
        return ON_OFFLINE;
    }

    public BindVoidEventBus<SessionCloseListener, Session> closeEvent() {
        return ON_CLOSE;
    }


}
