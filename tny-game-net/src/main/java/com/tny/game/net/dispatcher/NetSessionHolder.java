package com.tny.game.net.dispatcher;

import com.tny.game.log.CoreLogger;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.listener.SessionChangeEvent;
import com.tny.game.net.base.listener.SessionListener;
import com.tny.game.net.dispatcher.exception.ValidatorFailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class NetSessionHolder implements SessionHolder {

    protected static final Logger LOG = LoggerFactory.getLogger(CoreLogger.SESSION);

    private List<SessionListener> listenerList = new CopyOnWriteArrayList<SessionListener>();

    /**
     * <p>
     * <p>
     * 添加指定的session<br>
     *
     * @param session 指定的session
     * @throws ValidatorFailException
     */
    protected abstract boolean online(ServerSession session, LoginCertificate loginInfo) throws ValidatorFailException;

    /**
     * 移出所有组
     */
    protected abstract void removeAllChannel(String userGroup);

    protected void disconnect(Session session) {
        if (session instanceof NetSession)
            ((NetSession) session).disconnect();
        this.fireDisconnectSession(new SessionChangeEvent(this, session));
    }

    @Override
    public void addSessionListener(SessionListener listener) {
        this.listenerList.add(listener);
    }

    @Override
    public void addSessionListener(Collection<SessionListener> listeners) {
        this.listenerList.addAll(listeners);
    }

    @Override
    public void removeSessionListener(SessionListener listener) {
        this.listenerList.remove(listener);
    }

    @Override
    public void clearSessionListener() {
        this.listenerList.clear();
    }

    protected void fireDisconnectSession(SessionChangeEvent event) {
        for (SessionListener listener : this.listenerList) {
            try {
                listener.handleDisconnectSession(event);
            } catch (Exception e) {
                NetSessionHolder.LOG.error("fireAddSession exception", e);
            }
        }
    }

    protected void fireAddSession(SessionChangeEvent event) {
        for (SessionListener listener : this.listenerList) {
            try {
                listener.handleAddSession(event);
            } catch (Exception e) {
                NetSessionHolder.LOG.error("fireAddSession exception", e);
            }
        }
    }

    protected void fireRemoveSession(SessionChangeEvent event) {
        for (SessionListener listener : this.listenerList) {
            try {
                listener.handleRemoveSession(event);
            } catch (Exception e) {
                NetSessionHolder.LOG.error("fireRemoveSession exception", e);
            }
        }
    }
}
