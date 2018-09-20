package com.tny.game.net.transport;

import com.tny.game.common.event.*;
import com.tny.game.net.transport.listener.SessionListener;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-03 15:55
 */
interface SessionEvents {

    @SuppressWarnings("unchecked")
    BindP1EventBus<SessionListener, Session, Tunnel> ON_ONLINE =
            EventBuses.of(SessionListener.class, SessionListener::onOnline);

    @SuppressWarnings("unchecked")
    BindP1EventBus<SessionListener, Session, Tunnel> ON_OFFLINE =
            EventBuses.of(SessionListener.class, SessionListener::onOffline);

    @SuppressWarnings("unchecked")
    BindP1EventBus<SessionListener, Session, Tunnel> ON_CLOSE =
            EventBuses.of(SessionListener.class, SessionListener::onClose);


}
