package com.tny.game.net.common.session;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.Protocol;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class CommonSessionHolder<UID, S extends NetSession<UID>> extends AbstractNetSessionHolder<UID, S> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);
    // private static final Logger LOG_ENCODE = LoggerFactory.getLogger(CoreLogger.CODER);

    private final long sessionLife = 600000;

    protected final ConcurrentHashMap<String, Map<UID, S>> sessionMap = new ConcurrentHashMap<>();

    public CommonSessionHolder(long clearInterval) {
        ScheduledExecutorService sessionScanExecutor = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionScanWorker", true));
        sessionScanExecutor.scheduleAtFixedRate(this::clearInvalidedSession, clearInterval, clearInterval, TimeUnit.MILLISECONDS);
    }

    private void clearInvalidedSession() {
        long now = System.currentTimeMillis();
        for (Map<UID, S> userGroupSessionMap : this.sessionMap.values()) {
            userGroupSessionMap.forEach((key, session) -> {
                try {
                    session.removeTimeoutFuture();
                    if (session.getOfflineTime() + sessionLife > now) {
                        if (!session.isInvalided())
                            session.invalid();
                        if (session.isInvalided())
                            userGroupSessionMap.remove(session.getUID(), session);
                    }
                } catch (Throwable e) {
                    LOG.error("clear {} invalided session exception", session.getUID(), e);
                }
            });
        }
    }

    @Override
    public Session<UID> getSession(String userGroup, UID uid) {
        return this.getSession0(userGroup, uid);
    }

    @SuppressWarnings("unchecked")
    private S getSession0(String userGroup, UID uid) {
        Map<UID, S> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return null;
        return userGroupSessionMap.get(uid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<UID, Session<UID>> getSessionsByGroup(String userGroup) {
        Map<UID, S> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return Collections.emptyMap();
        return (Map<UID, Session<UID>>) userGroupSessionMap;
    }

    @Override
    public boolean isOnline(String userGroup, UID uid) {
        Session session = this.getSession0(userGroup, uid);
        return session != null && session.isOnline();
    }

    @Override
    public boolean send2User(String userGroup, UID uid, Protocol protocol, MessageContent content) {
        NetSession session = this.getSession0(userGroup, uid);
        if (session != null) {
            session.sendMessage(protocol, content);
            return true;
        }
        return false;
    }

    @Override
    public int send2Users(String userGroup, Collection<UID> uidColl, Protocol protocol, MessageContent<?> content) {
        return this.doSendMultiSessionID(userGroup, uidColl, protocol, content);
    }

    @Override
    public void send2AllOnline(String userGroup, Protocol protocol, MessageContent<?> content) {
        Map<UID, S> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return;
        this.doSendMultiSession(userGroupSessionMap.values(), protocol, content);
    }

    @Override
    public int size() {
        int size = 0;
        for (Map<UID, S> map : this.sessionMap.values())
            size += map.size();
        return size;
    }

    private void debugSessionSize() {
        if (CommonSessionHolder.LOG.isDebugEnabled())
            CommonSessionHolder.LOG.debug("#DefaultSessionHolder#会话管理器#会话数量为 {}", this.sessionMap.size());
    }

    @Override
    public int countOnline(String userGroup) {
        return this.sessionMap.size();
    }

    private void doSendMultiSession(Collection<S> sessionCollection, Protocol protocol, MessageContent<?> content) {
        for (NetSession session : sessionCollection)
            session.sendMessage(protocol, content);
    }

    private int doSendMultiSessionID(String userGroup, Collection<UID> uidColl, Protocol protocol, MessageContent<?> content) {
        int num = 0;
        for (UID uid : uidColl) {
            NetSession session = this.getSession0(userGroup, uid);
            if (session != null) {
                session.sendMessage(protocol, content);
                num++;
            }
        }
        return num;
    }


    @Override
    public Session<UID> offline(String userGroup, UID uid, boolean invalid) {
        NetSession<UID> session = this.getSession0(userGroup, uid);
        if (session != null)
            session.offline(invalid);
        return session;
    }

    @Override
    public void offlineAll(String userGroup, boolean invalid) {
        this.sessionMap.getOrDefault(userGroup, ImmutableMap.of())
                .forEach((key, session) -> session.offline(invalid));
    }

    @Override
    public void offlineAll(boolean invalid) {
        for (Map<UID, S> userGroupSessionMap : this.sessionMap.values()) {
            userGroupSessionMap.forEach((key, session) -> session.offline(invalid));
        }
    }

    @Override
    public Optional<S> online(S session, LoginCertificate<UID> cert) throws ValidatorFailException {
        if (!this.loginSession(session, cert))
            return Optional.empty();
        if (!session.isOnline())
            return Optional.empty();
        Map<UID, S> userGroupSessionMap = this.sessionMap.get(session.getGroup());
        if (userGroupSessionMap == null) {
            this.sessionMap.putIfAbsent(session.getGroup(), new ConcurrentHashMap<>());
            userGroupSessionMap = this.sessionMap.get(session.getGroup());
        }
        S oldSession = userGroupSessionMap.get(session.getUID());
        if (oldSession == session)
            return Optional.of(session);
        if (cert.isRelogin()) {
            // oldSession为null或者失效 session登陆ID不是同一个 session无法转换
            if (oldSession == null || oldSession.isInvalided() ||
                    oldSession.getCertificate().getID() != cert.getID() ||
                    !oldSession.reline(session)) {
                String account = cert.getUserGroup() + "-" + cert.getUserID();
                throw new ValidatorFailException(CoreResponseCode.SESSION_LOSS, account, session.getHostName());
            }
            session = oldSession;
        } else {
            oldSession = userGroupSessionMap.put(session.getUID(), session);
            if (oldSession != null && oldSession != session) {
                // 替换session成功, 并且任然存在oldSession
                if (oldSession.isOnline())
                    oldSession.offline(true);
                this.onRemoveSession.notify(this, oldSession);
            }
            if (oldSession != session) {
                this.onAddSession.notify(this, session);
                this.debugSessionSize();
            }
        }
        return Optional.of(session);
    }

    private boolean loginSession(NetSession<UID> session, LoginCertificate<UID> certificate) {
        if (!certificate.isLogin())
            return false;
        session.login(certificate);
        return true;
    }


}
