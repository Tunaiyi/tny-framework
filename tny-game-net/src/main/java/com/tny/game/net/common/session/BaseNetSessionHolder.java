package com.tny.game.net.common.session;


import com.tny.game.event.EventBuses;
import com.tny.game.log.NetLogger;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.Session;
import com.tny.game.net.session.holder.NetSessionHolder;
import com.tny.game.net.session.holder.SessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public abstract class BaseNetSessionHolder<UID, S extends NetSession<UID>> implements NetSessionHolder<UID, S> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    protected final BindP1EventBus<SessionHolderListener, SessionHolder, Session> onAddSession =
            EventBuses.of(SessionHolderListener.class, SessionHolderListener::onAddSession);

    protected final BindP1EventBus<SessionHolderListener, SessionHolder, Session> onRemoveSession =
            EventBuses.of(SessionHolderListener.class, SessionHolderListener::onRemoveSession);

    // protected void disconnect(NetSession<?> session) {
    //     session.offline();
    //     this.fireDisconnectSession(new SessionChangeEvent<>(this, session));
    // }

    @Override
    public void addListener(SessionHolderListener<UID> listener) {
        onAddSession.addListener(listener);
        onRemoveSession.addListener(listener);
    }

    @Override
    public void addListener(Collection<SessionHolderListener<UID>> listeners) {
        listeners.forEach(l -> {
            onAddSession.addListener(l);
            onAddSession.addListener(l);
        });
    }

    @Override
    public void removeListener(SessionHolderListener<UID> listener) {
        onAddSession.removeListener(listener);
        onRemoveSession.removeListener(listener);
    }

}
