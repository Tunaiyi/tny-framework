/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARSession IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.session;

import com.tny.game.common.event.bus.*;
import com.tny.game.net.application.*;
import com.tny.game.net.session.listener.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.tny.game.common.utils.ObjectAide.*;

@SuppressWarnings("unchecked")
public abstract class AbstractSessionKeeper implements NetSessionKeeper {

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    @SuppressWarnings("rawtypes")
    private final A1BindEvent<SessionKeeperListener, SessionKeeper, Session> onAddSession =
            Events.ofEvent(SessionKeeperListener.class, SessionKeeperListener::onAddSession);

    @SuppressWarnings("rawtypes")
    private final A1BindEvent<SessionKeeperListener, SessionKeeper, Session> onRemoveSession =
            Events.ofEvent(SessionKeeperListener.class, SessionKeeperListener::onRemoveSession);

    /* 所有 session */
    private final ContactType contactType;

    private final Map<Long, NetSession> sessionMap = new ConcurrentHashMap<>();

    protected AbstractSessionKeeper(ContactType contactType) {
        this.contactType = contactType;
    }

    protected void onOnline(Session session) {

    }

    protected void onOffline(Session session) {

    }

    protected void onClose(Session session) {

    }

    @Override
    public void notifyOnline(Session session) {
        if (!Objects.equals(this.getContactType(), session.getContactType())) {
            return;
        }
        this.onOnline(session);
    }

    @Override
    public void notifyOffline(Session session) {
        if (!Objects.equals(this.getContactType(), session.getContactType())) {
            return;
        }
        this.onOffline(session);
    }

    @Override
    public void notifyClose(Session session) {
        if (!Objects.equals(this.getContactType(), session.getContactType())) {
            return;
        }
        NetSession netSession = as(session);
        if (this.removeSession(netSession.getIdentify(), netSession)) {
            onClose(session);
        }
    }

    @Override
    public ContactType getContactType() {
        return this.contactType;
    }

    @Override
    public Session getSession(long identify) {
        return this.sessionMap.get(identify);
    }

    @Override
    public Map<Long, Session> getAllSessions() {
        return Collections.unmodifiableMap(this.sessionMap);
    }

    @Override
    public void sendTo(long identify, MessageContent context) {
        Session session = this.getSession(identify);
        if (session != null) {
            session.send(context);
        }
    }

    @Override
    public void sendTo(Collection<Long> identifiers, MessageContent context) {
        this.doSendMultiId(identifiers.stream(), context);
    }

    @Override
    public void sendTo(Stream<Long> identifiesStream, MessageContent context) {
        this.doSendMultiId(identifiesStream, context);
    }

    @Override
    public void send2AllOnline(MessageContent context) {
        for (Session session : this.sessionMap.values())
            session.send(context);
    }

    @Override
    public int size() {
        return this.sessionMap.size();
    }

    @Override
    public Session close(long identify) {
        Session session = this.sessionMap.get(identify);
        if (session != null) {
            session.close();
        }
        return session;
    }

    @Override
    public void closeAll() {
        this.sessionMap.forEach((key, session) -> session.close());
    }

    @Override
    public Session offline(long identify) {
        Session session = this.sessionMap.get(identify);
        if (session != null) {
            session.offline();
        }
        return session;
    }

    @Override
    public void offlineAll() {
        this.sessionMap.forEach((key, session) -> session.offline());
    }

    @Override
    public boolean isOnline(long identify) {
        Session session = this.getSession(identify);
        return session != null && session.isOnline();
    }

    @Override
    public int countOnlineSize() {
        int online = 0;
        for (Session session : this.sessionMap.values()) {
            if (session.isOnline()) {
                online++;
            }
        }
        return online;
    }

    protected NetSession findSession(long identify) {
        return this.sessionMap.get(identify);
    }

    protected boolean removeSession(long identify, NetSession existOne) {
        if (this.sessionMap.remove(identify, existOne)) {
            onRemoveSession.notify(this, existOne);
            return true;
        }
        return false;
    }

    protected void resetSession(long identify, NetSession newOne) {
        NetSession oldOne = this.sessionMap.put(identify, newOne);
        if (oldOne != null && oldOne != newOne) {
            oldOne.close();
            onRemoveSession.notify(this, oldOne);
        }
        onAddSession.notify(this, newOne);
    }

    @Override
    public void addListener(SessionKeeperListener listener) {
        onAddSession.addListener(listener);
        onRemoveSession.addListener(listener);
    }

    @Override
    public void addListener(Collection<SessionKeeperListener> listeners) {
        listeners.forEach(l -> {
            onAddSession.addListener(l);
            onRemoveSession.addListener(l);
        });
    }

    @Override
    public void removeListener(SessionKeeperListener listener) {
        onAddSession.removeListener(listener);
        onRemoveSession.removeListener(listener);
    }

    private void doSendMultiId(Stream<Long> identifies, MessageContent context) {
        identifies.forEach(identify -> {
            Session session = this.getSession(identify);
            if (session != null) {
                session.send(context);
            }
        });
    }

    protected void monitorSession() {
        int size = 0;
        int online = 0;
        for (NetSession session : this.sessionMap.values()) {
            size++;
            if (session.isOnline()) {
                online++;
            }
        }
        LOG.info("会话管理器#{} Group -> 会话数量为 {} | 在线数 {}", this.getContactType(), size, online);
    }

}
