package com.tny.game.net.transport;


import com.tny.game.common.event.*;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.transport.listener.SessionHolderListener;
import org.slf4j.*;

import java.util.Collection;

@SuppressWarnings("unchecked")
public abstract class AbstractSessionKeeper<UID> implements SessionKeeper<UID> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    protected static final BindP1EventBus<SessionHolderListener, SessionKeeper, Session> onAddSession =
            EventBuses.of(SessionHolderListener.class, SessionHolderListener::onAddSession);

    protected static final BindP1EventBus<SessionHolderListener, SessionKeeper, Session> onRemoveSession =
            EventBuses.of(SessionHolderListener.class, SessionHolderListener::onRemoveSession);

    private String userType;

    protected AbstractSessionKeeper(String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }

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
