package com.tny.game.net.common.session;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.config.Config;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.net.base.AppConstants;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.session.LoginCertificate;
import com.tny.game.net.session.NetSession;
import com.tny.game.net.session.Session;
import com.tny.game.net.tunnel.NetTunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Unit("CommonSessionHolder")
public class CommonSessionHolder extends AbstractNetSessionHolder {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);
    // private static final Logger LOG_ENCODE = LoggerFactory.getLogger(CoreLogger.CODER);

    private static final long DEFAULT_CLEAR_INTERVAL = 60000L;
    private static final long DEFAULT_SESSION_LIFE = 600000L;
    private static final long DEFAULT_KEEP_IDLE_TIME = 180000L;

    protected long sessionLife;
    protected long keepIdleTime;

    protected final ConcurrentHashMap<String, Map<Object, NetSession>> sessionMap = new ConcurrentHashMap<>();

    public CommonSessionHolder() {
        this(DEFAULT_SESSION_LIFE, DEFAULT_KEEP_IDLE_TIME, DEFAULT_CLEAR_INTERVAL);
    }

    public CommonSessionHolder(Config config) {
        this(config.getLong(AppConstants.SESSION_HOLDER_SESSION_LIVE, DEFAULT_SESSION_LIFE),
                config.getLong(AppConstants.SESSION_HOLDER_KEEP_IDLE_TIME, DEFAULT_KEEP_IDLE_TIME),
                config.getLong(AppConstants.SESSION_HOLDER_CLEAR_INTERVAL, DEFAULT_CLEAR_INTERVAL));
    }

    public CommonSessionHolder(long sessionLife, long keepIdleTime, long clearInterval) {
        this.sessionLife = sessionLife;
        this.keepIdleTime = keepIdleTime;
        ScheduledExecutorService sessionScanExecutor = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionScanWorker", true));
        sessionScanExecutor.scheduleAtFixedRate(this::clearInvalidedSession, clearInterval, clearInterval, TimeUnit.MILLISECONDS);
    }

    private void clearInvalidedSession() {
        long now = System.currentTimeMillis();
        for (Map<Object, NetSession> userGroupSessionMap : this.sessionMap.values()) {
            userGroupSessionMap.forEach((key, session) -> {
                try {
                    NetTunnel<?> tunnel = session.getCurrentTunnel();
                    if (tunnel.getLatestActiveAt() + keepIdleTime < now) {
                        LOG.warn("服务器主动关闭空闲终端 : {}", tunnel);
                        tunnel.close();
                    }
                    NetSession<?> closeSession = null;
                    if (session.isClosed()) {
                        userGroupSessionMap.remove(session.getUID(), session);
                        closeSession = session;
                    } else if (session.isOffline() && session.getOfflineTime() + sessionLife < now) {
                        session.close();
                        closeSession = session;
                    }
                    if (closeSession != null)
                        userGroupSessionMap.remove(session.getUID(), session);
                } catch (Throwable e) {
                    LOG.error("clear {} invalided session exception", session.getUID(), e);
                }
            });
        }
    }

    @Override
    public <U> Session<U> getSession(String userGroup, U uid) {
        return this.getSession0(userGroup, uid);
    }

    @SuppressWarnings("unchecked")
    private <U> NetSession<U> getSession0(String userGroup, U uid) {
        Map<Object, NetSession> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return null;
        return userGroupSessionMap.get(uid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Map<U, Session<U>> getSessionsByGroup(String userGroup) {
        Map<Object, ? extends Session> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return Collections.emptyMap();
        return (Map<U, Session<U>>) userGroupSessionMap;
    }

    @Override
    public boolean isOnline(String userGroup, Object uid) {
        Session session = this.getSession0(userGroup, uid);
        return session != null && session.isOnline();
    }

    @Override
    public boolean send2User(String userGroup, Object uid, MessageContent content) {
        NetSession<Object> session = this.getSession0(userGroup, uid);
        if (session != null) {
            session.send(content);
            return true;
        }
        return false;
    }

    @Override
    public int send2Users(String userGroup, Collection<?> uidColl, MessageContent<?> content) {
        return this.doSendMultiSessionID(userGroup, uidColl, content);
    }

    @Override
    public void send2AllOnline(String userGroup, MessageContent<?> content) {
        Map<Object, NetSession> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return;
        this.doSendMultiSession(userGroupSessionMap.values(), content);
    }

    @Override
    public int size() {
        int size = 0;
        for (Map<Object, NetSession> map : this.sessionMap.values())
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

    private void doSendMultiSession(Collection<NetSession> sessionCollection, MessageContent<?> content) {
        for (NetSession<?> session : sessionCollection)
            session.send(content);
    }

    private int doSendMultiSessionID(String userGroup, Collection<?> uidColl, MessageContent<?> content) {
        int num = 0;
        for (Object uid : uidColl) {
            NetSession<Object> session = this.getSession0(userGroup, uid);
            if (session != null) {
                session.send(content);
                num++;
            }
        }
        return num;
    }


    @Override
    public <U> Session<U> offline(String userGroup, U uid, boolean invalid) {
        NetSession<U> session = this.getSession0(userGroup, uid);
        if (session != null) {
            if (invalid)
                session.close();
            else
                session.offline();
        }
        return session;
    }

    @Override
    public void offlineAll(String userGroup, boolean invalid) {
        this.sessionMap.getOrDefault(userGroup, ImmutableMap.of())
                .forEach((key, session) -> {
                    if (invalid)
                        session.close();
                    else
                        session.offline();
                });
    }

    @Override
    public void offlineAll(boolean invalid) {
        for (Map<Object, NetSession> userGroupSessionMap : this.sessionMap.values()) {
            userGroupSessionMap.forEach((key, session) -> {
                if (invalid)
                    session.close();
                else
                    session.offline();
            });
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> boolean online(NetSession<U> session, LoginCertificate<U> cert) throws ValidatorFailException {
        if (!this.loginSession(session, cert))
            return false;
        if (!session.isOnline())
            return false;
        Map<Object, NetSession> userGroupSessionMap = this.sessionMap.get(session.getUserGroup());
        if (userGroupSessionMap == null) {
            this.sessionMap.putIfAbsent(session.getUserGroup(), new ConcurrentHashMap<>());
            userGroupSessionMap = this.sessionMap.get(session.getUserGroup());
        }
        NetSession oldSession = userGroupSessionMap.get(session.getUID());
        if (oldSession == session)
            return true;
        if (cert.isRelogin()) {
            oldSession.offline();
            // oldSession为null或者失效 session登陆ID不是同一个 session无法转换
            if (oldSession == null || oldSession.isClosed() || !oldSession.relogin(session)) {
                throw new ValidatorFailException(CoreResponseCode.SESSION_LOSS);
            }
        } else {
            oldSession = userGroupSessionMap.put(session.getUID(), session);
            if (oldSession != null && oldSession != session) {
                // 替换session成功, 并且任然存在oldSession
                if (oldSession.isOnline())
                    oldSession.close();
                this.onRemoveSession.notify(this, oldSession);
            }
            if (oldSession != session) {
                this.onAddSession.notify(this, session);
                this.debugSessionSize();
            }
        }
        return true;
    }

    private <UID> boolean loginSession(NetSession<UID> session, LoginCertificate<UID> certificate) throws ValidatorFailException {
        if (!certificate.isLogin())
            return false;
        session.login(certificate);
        return true;
    }


}
