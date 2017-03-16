package com.tny.game.net.dispatcher;

import com.tny.game.event.BindP1EventBus;
import com.tny.game.event.EventBuses;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.listener.SessionHolderListener;
import com.tny.game.net.session.Session;
import com.tny.game.net.session.SessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@SuppressWarnings("unchecked")
public abstract class AbstractNetSessionHolder implements NetSessionHolder {

    protected static final Logger LOG = LoggerFactory.getLogger(CoreLogger.SESSION);

    protected final BindP1EventBus<SessionHolderListener, SessionHolder, Session> onAddSession =
            EventBuses.of(SessionHolderListener.class, SessionHolderListener::onAddSession);

    protected final BindP1EventBus<SessionHolderListener, SessionHolder, Session> onRemoveSession =
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
