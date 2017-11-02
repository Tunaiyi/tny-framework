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
import com.tny.game.net.tunnel.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.server.UID;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.tny.game.common.utils.ObjectAide.*;

@Unit("CommonSessionHolder")
public class CommonSessionHolder extends AbstractNetSessionHolder {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);
    // private static final Logger LOG_ENCODE = LoggerFactory.getLogger(CoreLogger.CODER);

    private static final long DEFAULT_CLEAR_INTERVAL = 60000L;
    private static final long DEFAULT_SESSION_LIFE = 600000L;
    private static final long DEFAULT_KEEP_IDLE_TIME = 180000L;
    private static final int DEFAULT_SESSION_OFFLINE_SIZE = 1024;

    protected long sessionLife;
    protected int sessionOfflineSize;
    protected long keepIdleTime;

    protected final ConcurrentHashMap<String, Map<Object, NetSession>> sessionMap = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Queue<NetSession>> offlineSessionMap = new ConcurrentHashMap<>();

    public CommonSessionHolder() {
        this(DEFAULT_SESSION_LIFE, DEFAULT_SESSION_OFFLINE_SIZE, DEFAULT_KEEP_IDLE_TIME, DEFAULT_CLEAR_INTERVAL);
    }

    public CommonSessionHolder(Config config) {
        this(config.getLong(AppConstants.SESSION_HOLDER_SESSION_LIVE, DEFAULT_SESSION_LIFE),
                config.getInt(AppConstants.SESSION_HOLDER_SESSION_OFFLINE_SIZE, DEFAULT_SESSION_OFFLINE_SIZE),
                config.getLong(AppConstants.SESSION_HOLDER_KEEP_IDLE_TIME, DEFAULT_KEEP_IDLE_TIME),
                config.getLong(AppConstants.SESSION_HOLDER_CLEAR_INTERVAL, DEFAULT_CLEAR_INTERVAL));
    }

    public CommonSessionHolder(long sessionLife, int sessionOfflineSize, long keepIdleTime, long clearInterval) {
        this.sessionLife = sessionLife;
        this.keepIdleTime = keepIdleTime;
        this.sessionOfflineSize = sessionOfflineSize;
        ScheduledExecutorService sessionScanExecutor = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionScanWorker", true));
        sessionScanExecutor.scheduleAtFixedRate(this::clearInvalidedSession, clearInterval, clearInterval, TimeUnit.MILLISECONDS);
        NetSession.ON_OFFLINE.add(this::onOffline);
        NetSession.ON_ONLINE.add(this::onOnline);
        NetSession.ON_CLOSE.add(this::onClose);
    }

    private Queue<NetSession> offlineSessionQueue(String group) {
        return offlineSessionMap.computeIfAbsent(group, (k) -> new ConcurrentLinkedQueue<>());
    }

    private void onOnline(Session<UID> session, Tunnel<UID> tunnel) {
        offlineSessionQueue(session.getUserGroup()).remove(session);
    }

    private void onOffline(Session<UID> session, Tunnel<UID> tunnel) {
        if (session.isOffline())
            offlineSessionQueue(session.getUserGroup()).add(as(session));
    }

    private void onClose(Session<UID> session, Tunnel<UID> tunnel) {
        Map<Object, ? extends Session> userGroupSessionMap = this.sessionMap.get(session.getUserGroup());
        userGroupSessionMap.remove(session.getUID(), session);
        this.offlineSessionQueue(session.getUserGroup()).remove(session);
    }

    @Override
    public <U> Session<U> getSession(String userGroup, U uid) {
        return this.getSession0(userGroup, uid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> Map<U, Session<U>> getSessionsByGroup(String userGroup) {
        Map<Object, ? extends Session> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return Collections.emptyMap();
        return as(Collections.unmodifiableMap(userGroupSessionMap));
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
    public void send2Users(String userGroup, Collection<?> uidColl, MessageContent<?> content) {
        this.doSendMultiSessionID(userGroup, uidColl.stream(), content);
    }

    @Override
    public void send2Users(String userGroup, Stream<?> stream, MessageContent<?> content) {
        this.doSendMultiSessionID(userGroup, stream, content);
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
        this.sessionMap.forEach((key, value) -> LOG.info("会话管理器#{} Group -> 会话数量为 {}", key, value.size()));
        this.offlineSessionMap.forEach((key, value) -> LOG.info("会话管理器#{} Group -> 离线会话数量为 {} / {}", key, value.size(), this.sessionOfflineSize));
    }

    @Override
    public int countOnline(String userGroup) {
        return this.sessionMap.size();
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
            if (oldSession != null)
                oldSession.offline();
            if (oldSession == null) {
                LOG.warn("旧session {} 已经丢失", session.getUID());
                throw new ValidatorFailException(CoreResponseCode.SESSION_LOSS);
            }
            if (oldSession.isClosed()) {
                LOG.warn("旧session {} 已经关闭", session.getUID());
                throw new ValidatorFailException(CoreResponseCode.SESSION_LOSS);
            }
            if (!oldSession.relogin(session)) {
                throw new ValidatorFailException(CoreResponseCode.SESSION_LOSS);
            }
        } else {
            oldSession = userGroupSessionMap.put(session.getUID(), session);
            if (oldSession != null && oldSession != session) {
                // 替换session成功, 并且任然存在oldSession
                if (!oldSession.isClosed())
                    oldSession.close();
                onRemoveSession.notify(this, oldSession);
            }
            if (oldSession != session) {
                onAddSession.notify(this, session);
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

    @SuppressWarnings("unchecked")
    private <U> NetSession<U> getSession0(String userGroup, U uid) {
        Map<Object, NetSession> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return null;
        return userGroupSessionMap.get(uid);
    }

    private void doSendMultiSession(Collection<NetSession> sessionCollection, MessageContent<?> content) {
        for (NetSession<?> session : sessionCollection)
            session.send(content);
    }

    private void doSendMultiSessionID(String userGroup, Stream<?> uidColl, MessageContent<?> content) {
        uidColl.forEach(uid -> {
            NetSession<Object> session = this.getSession0(userGroup, uid);
            if (session != null) {
                session.send(content);
            }
        });
    }

    private void clearInvalidedSession() {
        long now = System.currentTimeMillis();
        // int size = this.offlineSessions.size() - sessionOfflineSize;
        Set<NetSession> closeSessions = new HashSet<>();
        offlineSessionMap.forEach((group, offlineSessions) -> {
                    Map<Object, Session<Object>> groupMap = getSessionsByGroup(group);
                    for (NetSession<?> session : offlineSessions) {
                        try {
                            NetTunnel<?> tunnel = session.getCurrentTunnel();
                            if (tunnel.isConnected() && tunnel.getLatestActiveAt() + keepIdleTime < now) {
                                LOG.warn("服务器主动关闭空闲终端 : {}", tunnel);
                                tunnel.close();
                            }

                            NetSession<?> closeSession = null;
                            if (session.isClosed()) {
                                LOG.info("移除已关闭的OfflineSession {}", session.getUID());
                                closeSession = session;
                            } else if (session.isOffline() && session.getOfflineTime() + sessionLife < now) {
                                LOG.info("移除下线超时的OfflineSession {}", session.getUID());
                                session.close();
                                closeSession = session;
                            }
                            if (closeSession != null) {
                                groupMap.remove(session.getUID(), session);
                                closeSessions.add(session);
                            }
                        } catch (Throwable e) {
                            LOG.error("clear {} invalided session exception", session.getUID(), e);
                        }
                    }
                    offlineSessions.removeAll(closeSessions);
                    int size = offlineSessions.size() - sessionOfflineSize;
                    if (sessionOfflineSize > 0 && size > 0) {
                        for (int i = 0; i < size; i++) {
                            NetSession<?> session = offlineSessions.poll();
                            if (session == null)
                                continue;
                            try {
                                LOG.info("关闭第{}个 超过{}数量的OfflineSession {}", i, size, session.getUID());
                                session.close();
                            } catch (Throwable e) {
                                LOG.error("clear {} overmuch offline session exception", session.getUID(), e);
                            }
                        }
                    }
                }

        );
        debugSessionSize();
        // for (Map<Object, NetSession> userGroupSessionMap : this.sessionMap.values()) {
        //     userGroupSessionMap.forEach((key, session) -> {
        //         try {
        //             NetTunnel<?> tunnel = session.getCurrentTunnel();
        //             if (tunnel.isConnected() && tunnel.getLatestActiveAt() + keepIdleTime < now) {
        //                 LOG.warn("服务器主动关闭空闲终端 : {}", tunnel);
        //                 tunnel.close();
        //             }
        //             NetSession<?> closeSession = null;
        //             if (session.isClosed()) {
        //                 userGroupSessionMap.remove(session.getUID(), session);
        //                 closeSession = session;
        //             } else if (session.isOffline() && session.getOfflineTime() + sessionLife < now) {
        //                 session.close();
        //                 closeSession = session;
        //             }
        //             if (closeSession != null)
        //                 userGroupSessionMap.remove(session.getUID(), session);
        //         } catch (Throwable e) {
        //             LOG.error("clear {} invalided session exception", session.getUID(), e);
        //         }
        //     });
        // }
    }

}
