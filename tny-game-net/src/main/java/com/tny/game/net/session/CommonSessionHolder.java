package com.tny.game.net.session;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.concurrent.CoreThreadFactory;
import com.tny.game.common.config.Config;
import com.tny.game.net.base.*;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.tunnel.NetTunnel;
import com.tny.game.net.utils.NetConfigs;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static com.tny.game.common.utils.ObjectAide.as;
import static com.tny.game.net.utils.NetConfigs.*;

@Unit("CommonSessionHolder")
public class CommonSessionHolder extends AbstractNetSessionHolder {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    private long offlineSessionLifeTime;
    private int offlineSessionMaxSize;
    private long keepIdleTime;

    private final Map<String, Map<Object, NetSession>> sessionMap = new ConcurrentHashMap<>();
    private final Map<String, Queue<NetSession>> offlineSessionMap = new ConcurrentHashMap<>();

    public CommonSessionHolder() {
        this(SESSION_HOLDER_DEFAULT_OFFLINE_SESSION_LIFE_TIME,
                SESSION_HOLDER_DEFAULT_OFFLINE_SESSION_MAX_SIZE,
                SESSION_HOLDER_DEFAULT_KEEP_IDLE_TIME,
                SESSION_HOLDER_DEFAULT_CLEAR_INTERVAL);
    }

    public CommonSessionHolder(Config config) {
        this(config.getLong(SESSION_HOLDER_OFFLINE_SESSION_LIFE_TIME, SESSION_HOLDER_DEFAULT_OFFLINE_SESSION_LIFE_TIME),
                config.getInt(NetConfigs.SESSION_HOLDER_OFFLINE_SESSION_MAX_SIZE, SESSION_HOLDER_DEFAULT_OFFLINE_SESSION_MAX_SIZE),
                config.getLong(NetConfigs.SESSION_HOLDER_KEEP_IDLE_TIME, SESSION_HOLDER_DEFAULT_KEEP_IDLE_TIME),
                config.getLong(NetConfigs.SESSION_HOLDER_CLEAR_INTERVAL, SESSION_HOLDER_DEFAULT_CLEAR_INTERVAL));
    }

    public CommonSessionHolder(long offlineSessionLive, int offlineSessionMaxSize, long keepIdleTime, long clearInterval) {
        this.offlineSessionLifeTime = offlineSessionLive;
        this.keepIdleTime = keepIdleTime;
        this.offlineSessionMaxSize = offlineSessionMaxSize;
        ScheduledExecutorService sessionScanExecutor = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionScanWorker", true));
        sessionScanExecutor.scheduleAtFixedRate(this::clearInvalidedSession, clearInterval, clearInterval, TimeUnit.MILLISECONDS);
        NetSession.ON_OFFLINE.add((session, tunnel) -> onOffline(session));
        NetSession.ON_ONLINE.add((session, tunnel) -> onOnline(session));
        NetSession.ON_CLOSE.add((session, tunnel) -> onClose(session));
    }

    private Queue<NetSession> offlineSessionQueue(String group) {
        return offlineSessionMap.computeIfAbsent(group, (k) -> new ConcurrentLinkedQueue<>());
    }

    private void onOnline(Session<?> session) {
        Queue<? extends Session> sessions = offlineSessionQueue(session.getUserGroup());
        sessions.remove(session);
    }

    private void onOffline(Session<?> session) {
        if (session.isLogin() && session.isOffline()) {
            Queue<? extends Session> sessions = offlineSessionQueue(session.getUserGroup());
            sessions.add(as(session));
        }
    }

    private void onClose(Session<?> session) {
        if (!session.isLogin())
            return;
        Map<Object, ? extends Session> userGroupSessionMap = this.sessionMap.get(session.getUserGroup());
        userGroupSessionMap.remove(session.getUid(), session);
        Queue<? extends Session> sessions = this.offlineSessionQueue(session.getUserGroup());
        sessions.remove(session);
    }

    @Override
    public <U> Session<U> getSession(String userGroup, U uid) {
        return this.getNetSession(userGroup, uid);
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
        Session session = this.getNetSession(userGroup, uid);
        return session != null && session.isOnline();
    }

    @Override
    public <U> void send2User(String userGroup, U uid, MessageContent<U> content) {
        NetSession<U> session = this.getNetSession(userGroup, uid);
        if (session != null) {
            session.send(content);
        }
    }

    @Override
    public <U> void send2Users(String userGroup, Collection<U> uidColl, MessageContent<U> content) {
        this.doSendMultiSessionID(userGroup, uidColl.stream(), content);
    }

    @Override
    public <U> void send2Users(String userGroup, Stream<U> stream, MessageContent<U> content) {
        this.doSendMultiSessionID(userGroup, stream, content);
    }

    @Override
    public <U> void send2AllOnline(String userGroup, MessageContent<U> content) {
        Map<Object, NetSession> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return;
        for (@SuppressWarnings("unchecked") NetSession<U> session : userGroupSessionMap.values())
            session.send(content);
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
        this.offlineSessionMap.forEach((key, value) -> LOG.info("会话管理器#{} Group -> 离线会话数量为 {} / {}", key, value.size(), this.offlineSessionMaxSize));
    }

    @Override
    public int countOnline(String userGroup) {
        return this.sessionMap.size();
    }

    @Override
    public <U> Session<U> offline(String userGroup, U uid, boolean invalid) {
        NetSession<U> session = this.getNetSession(userGroup, uid);
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
    public <U> boolean online(NetTunnel<U> tunnel, NetCertificate<U> cert) throws ValidatorFailException {
        NetSession<U> session = tunnel.getSession();
        if (!this.loginSession(session, cert))
            return false;
        if (!session.isOnline())
            return false;
        //获取userGroup 与旧 session
        NetSession<U> oldSession = getNetSession(cert.getUserGroup(), cert.getUserId());
        if (oldSession == session)
            return true;
        if (cert.isRelogin()) { // 处理重登
            doSessionMerge(session, oldSession);
        } else { // 处理登录
            doSessionLogin(cert, session, oldSession);
        }
        return true;
    }

    private <U> void doSessionMerge(NetSession<U> session, NetSession<U> oldSession) throws ValidatorFailException {
        if (oldSession == null) { // 旧 session 失效
            LOG.warn("旧session {} 已经丢失", session.getUid());
            throw new ValidatorFailException(NetResponseCode.SESSION_LOSS);
        }
        if (oldSession.isClosed()) { // 旧 session 已经关闭(失效)
            LOG.warn("旧session {} 已经关闭", session.getUid());
            throw new ValidatorFailException(NetResponseCode.SESSION_LOSS);
        }
        oldSession.offline(); // 将旧 session 的 Tunnel T 下线
        oldSession.mergeSession(session);
    }

    @SuppressWarnings("unchecked")
    private <U> void doSessionLogin(NetCertificate<U> cert, NetSession<U> session, NetSession<U> oldSession) throws ValidatorFailException {
        if (oldSession != null) { // 如果旧 session 存在
            NetCertificate<?> oldCert = oldSession.getCertificate();
            if (cert.getId() < oldCert.getId()) { // 判断当前授权 ID 是旧的, 如果是旧的则无法登录
                LOG.warn("旧session {} 已经关闭", session.getUid());
                throw new ValidatorFailException(NetResponseCode.INVALID_CERTIFICATE);
            }
        }
        Map<Object, NetSession> userGroupSessionMap = this.sessionMap.computeIfAbsent(session.getUserGroup(), k -> new ConcurrentHashMap<>());
        oldSession = userGroupSessionMap.put(session.getUid(), session);
        if (oldSession != null && oldSession != session) {
            // 替换session成功, 并且任然存在oldSession
            if (!oldSession.isClosed())
                oldSession.close();
            onRemoveSession.notify(this, oldSession);
        }
        if (oldSession != session) { // 防止同一个 session 添加多次
            onAddSession.notify(this, session);
            this.debugSessionSize();
        }
    }

    private <U> boolean loginSession(NetSession<U> session, NetCertificate<U> certificate) throws ValidatorFailException {
        if (!certificate.isLogin())
            return false;
        session.login(certificate);
        return true;
    }

    @SuppressWarnings("unchecked")
    private <U> NetSession<U> getNetSession(String userGroup, U uid) {
        Map<Object, NetSession> userGroupSessionMap = this.sessionMap.get(userGroup);
        if (userGroupSessionMap == null)
            return null;
        return userGroupSessionMap.get(uid);
    }

    private <U> void doSendMultiSessionID(String userGroup, Stream<U> uidColl, MessageContent<U> content) {
        uidColl.forEach(uid -> {
            NetSession<U> session = this.getNetSession(userGroup, uid);
            if (session != null) {
                session.send(content);
            }
        });
    }

    /**
     * 清楚失效 Session
     */
    private void clearInvalidedSession() {
        long now = System.currentTimeMillis();
        // int size = this.offlineSessions.size() - offlineSessionMaxSize;
        Set<NetSession> closeSessions = new HashSet<>();
        offlineSessionMap.forEach((group, offlineSessions) -> {
                    Map<Object, ? extends Session> groupMap = this.sessionMap.get(group);
                    for (NetSession<?> session : offlineSessions) {
                        try {
                            NetTunnel<?> tunnel = session.getCurrentTunnel();
                            if (tunnel.isClosed() && tunnel.getLastReadAt() + keepIdleTime < now) {
                                LOG.warn("服务器主动关闭空闲终端 : {}", tunnel);
                                tunnel.close();
                            }

                            NetSession<?> closeSession = null;
                            if (session.isClosed()) {
                                LOG.info("移除已关闭的OfflineSession {}", session.getUid());
                                closeSession = session;
                            } else if (session.isOffline() && session.getOfflineTime() + offlineSessionLifeTime < now) {
                                LOG.info("移除下线超时的OfflineSession {}", session.getUid());
                                session.close();
                                closeSession = session;
                            }
                            if (closeSession != null) {
                                if (groupMap != null)
                                    groupMap.remove(closeSession.getUid(), closeSession);
                                closeSessions.add(closeSession);
                            }
                        } catch (Throwable e) {
                            LOG.error("clear {} invalided session exception", session.getUid(), e);
                        }
                    }
                    offlineSessions.removeAll(closeSessions);
                    int size = offlineSessions.size() - offlineSessionMaxSize;
                    if (offlineSessionMaxSize > 0 && size > 0) {
                        for (int i = 0; i < size; i++) {
                            NetSession<?> session = offlineSessions.poll();
                            if (session == null)
                                continue;
                            try {
                                LOG.info("关闭第{}个 超过{}数量的OfflineSession {}", i, size, session.getUid());
                                session.close();
                            } catch (Throwable e) {
                                LOG.error("clear {} overmuch offline session exception", session.getUid(), e);
                            }
                        }
                    }
                }

        );
        debugSessionSize();
    }

}
