package com.tny.game.net.session;


import com.tny.game.common.event.BindP1EventBus;
import com.tny.game.common.event.EventBuses;
import com.tny.game.suite.app.NetLogger;
import com.tny.game.net.session.holder.NetSessionHolder;
import com.tny.game.net.session.holder.SessionHolder;
import com.tny.game.net.session.holder.listener.SessionHolderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@SuppressWarnings("unchecked")
public abstract class AbstractNetSessionHolder implements NetSessionHolder {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    protected static final BindP1EventBus<SessionHolderListener, SessionHolder, Session> onAddSession =
            EventBuses.of(SessionHolderListener.class, SessionHolderListener::onAddSession);

    protected static final BindP1EventBus<SessionHolderListener, SessionHolder, Session> onRemoveSession =
            EventBuses.of(SessionHolderListener.class, SessionHolderListener::onRemoveSession);

    // protected void disconnect(NetSession<?> session) {
    //     session.offline();
    //     this.fireDisconnectSession(new SessionChangeEvent<>(this, session));
    // }

    @Override
    public void addListener(SessionHolderListener listener) {
        onAddSession.addListener(listener);
        onRemoveSession.addListener(listener);
    }

    @Override
    public void addListener(Collection<SessionHolderListener> listeners) {
        listeners.forEach(l -> {
            onAddSession.addListener(l);
            onAddSession.addListener(l);
        });
    }

    @Override
    public void removeListener(SessionHolderListener listener) {
        onAddSession.removeListener(listener);
        onRemoveSession.removeListener(listener);
    }

}
