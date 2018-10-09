package com.tny.game.net.transport;

import com.tny.game.common.concurrent.CoreThreadFactory;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.message.MessageSubject;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

public class CommonSessionKeeper<UID> extends AbstractSessionKeeper<UID> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    private SessionKeeperConfigurer<UID> configurer;

    private final Map<UID, NetSession<UID>> sessionMap = new ConcurrentHashMap<>();
    private final Queue<NetSession<UID>> offlineSessionQueue = new ConcurrentLinkedQueue<>();
    private SessionFactory<UID> sessionFactory;

    public CommonSessionKeeper(String userType, SessionKeeperConfigurer<UID> configurer) {
        super(userType);
        this.sessionFactory = configurer.getSessionFactory();
        this.configurer = configurer;
        ScheduledExecutorService sessionScanExecutor = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionScanWorker", true));
        sessionScanExecutor.scheduleAtFixedRate(this::clearInvalidedSession, configurer.getClearInterval(), configurer.getClearInterval(), TimeUnit.MILLISECONDS);
        SessionEvents.ON_ONLINE.add(this::onOnline);
        SessionEvents.ON_OFFLINE.add(this::onOffline);
        SessionEvents.ON_ACCEPT.add((session, tunnel) -> onOnline(session));
        SessionEvents.ON_CLOSE.add(this::onClose);
    }

    @Override
    public Session<UID> getSession(UID userId) {
        return this.sessionMap.get(userId);
    }

    @Override
    public Map<UID, Session<UID>> getAllSessions() {
        return Collections.unmodifiableMap(this.sessionMap);
    }

    @Override
    public boolean isOnline(UID userId) {
        Session session = this.getNetSession(userId);
        return session != null && session.isOnline();
    }

    @Override
    public void send2User(UID userId, MessageSubject subject) {
        NetSession<UID> session = this.getNetSession(userId);
        if (session != null) {
            session.sendAsyn(subject);
        }
    }

    @Override
    public void send2Users(Collection<UID> userIds, MessageSubject subject) {
        this.doSendMultiSessionID(userIds.stream(), subject);
    }

    @Override
    public void send2Users(Stream<UID> userIdsStream, MessageSubject subject) {
        this.doSendMultiSessionID(userIdsStream, subject);
    }

    @Override
    public void send2AllOnline(MessageSubject subject) {
        for (NetSession<UID> session : this.sessionMap.values())
            session.sendAsyn(subject);
    }

    @Override
    public int size() {
        return this.sessionMap.size();
    }

    @Override
    public int countOnline() {
        int online = 0;
        for (NetSession<UID> session : this.sessionMap.values()) {
            if (session.isOnline())
                online++;
        }
        return online;
    }

    @Override
    public Session<UID> offline(UID userId) {
        NetSession<UID> session = this.sessionMap.get(userId);
        if (session != null)
            session.offline();
        return session;
    }

    @Override
    public void offlineAll() {
        this.sessionMap.forEach((key, session) -> session.offline());
    }

    @Override
    public Session<UID> close(UID userId) {
        NetSession<UID> session = this.sessionMap.get(userId);
        if (session != null)
            session.close();
        return session;
    }

    @Override
    public void closeAll() {
        this.sessionMap.forEach((key, session) -> session.close());
    }

    @Override
    public boolean online(NetTunnel<UID> tunnel) throws ValidatorFailException {
        Certificate<UID> certificate = tunnel.getCertificate();
        if (!certificate.isAutherized())
            throw new ValidatorFailException(NetResultCode.VALIDATOR_FAIL, format("cart {} is unauthentic", certificate));
        if (!this.getUserType().equals(certificate.getUserType()))
            throw new ValidatorFailException(NetResultCode.VALIDATOR_FAIL, format("cart {} userType is {}, not {}", certificate, certificate.getUserType(), this.getUserType()));
        //获取userGroup 与旧 session
        if (certificate.getStatus() == CertificateStatus.AUTHERIZED) { // 原有 Session 接受新 Tunnel
            SessionFactory<UID> sessionFactory = this.getSessionFactory();
            NetSession<UID> session = sessionFactory.createSession(certificate);
            doNewSession(tunnel, session);
        } else { // 增加新 Session
            doAcceptTunnel(tunnel);
        }
        return true;
    }

    @Override
    public SessionFactory<UID> getSessionFactory() {
        return sessionFactory;
    }

    private void doAcceptTunnel(NetTunnel<UID> newTunnel) throws ValidatorFailException {
        Certificate<UID> certificate = newTunnel.getCertificate();
        NetSession<UID> existSession = this.sessionMap.get(certificate.getUserId());
        if (existSession == null) { // 旧 session 失效
            LOG.warn("旧session {} 已经丢失", newTunnel.getUserId());
            throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
        }
        if (existSession.isClosed()) { // 旧 session 已经关闭(失效)
            LOG.warn("旧session {} 已经关闭", newTunnel.getUserId());
            throw new ValidatorFailException(NetResultCode.SESSION_LOSS);
        }
        // existSession.offline(); // 将旧 session 的 Tunnel T 下线
        existSession.acceptTunnel(newTunnel);
    }

    @SuppressWarnings("unchecked")
    private void doNewSession(NetTunnel<UID> newTunnel, NetSession<UID> session) throws ValidatorFailException {
        Certificate<UID> certificate = newTunnel.getCertificate();
        while (true) {
            NetSession<UID> oldSession = this.sessionMap.get(certificate.getUserId());
            if (oldSession != null) { // 如果旧 session 存在
                Certificate<UID> oldCert = oldSession.getCertificate();
                // 判断新授权是否比原有授权时间早, 如果是则无法登录
                if (certificate.getId() != oldCert.getId() && !certificate.isNewerThan(session.getCertificate())) {
                    LOG.warn("认证已过 {}", certificate);
                    throw new ValidatorFailException(NetResultCode.INVALID_CERTIFICATE);
                }
            }
            if (oldSession != null) {
                if (!this.sessionMap.remove(certificate.getUserId(), oldSession))
                    continue;
                if (!oldSession.isClosed())
                    oldSession.close();
                onRemoveSession.notify(this, oldSession);
            }
            NetSession<UID> other = this.sessionMap.putIfAbsent(session.getUserId(), session);
            if (other != null)
                continue;
            session.acceptTunnel(newTunnel);
            onAddSession.notify(this, session);
            this.debugSessionSize();
            return;
        }
    }

    private NetSession<UID> getNetSession(UID userId) {
        return this.sessionMap.get(userId);
    }

    private void doSendMultiSessionID(Stream<UID> userIds, MessageSubject subject) {
        userIds.forEach(userId -> {
            NetSession<UID> session = this.getNetSession(userId);
            if (session != null) {
                session.sendAsyn(subject);
            }
        });
    }

    private void onOnline(Session<?> session) {
        if (!this.getUserType().equals(session.getUserType()))
            return;
        NetSession<UID> netSession = as(session);
        this.offlineSessionQueue.remove(netSession);
    }

    private void onOffline(Session<?> session) {
        if (!this.getUserType().equals(session.getUserType()))
            return;
        if (session.isLogin() && session.isOffline()) {
            this.offlineSessionQueue.add(as(session));
        }
    }

    private void onClose(Session<?> session) {
        if (!this.getUserType().equals(session.getUserType()))
            return;
        if (!session.isLogin())
            return;
        NetSession<UID> netSession = as(session);
        this.sessionMap.remove(netSession.getUserId(), netSession);
        this.offlineSessionQueue.remove(netSession);
    }

    /**
     * 清楚失效 Session
     */
    private void clearInvalidedSession() {
        long now = System.currentTimeMillis();
        // int size = this.offlineSessions.size() - offlineSessionMaxSize;
        Set<NetSession> closeSessions = new HashSet<>();
        offlineSessionQueue.forEach(session -> {
                    try {
                        NetSession<UID> closeSession = null;
                        if (session.isClosed()) {
                            LOG.info("移除已关闭的OfflineSession {}", session.getUserId());
                            closeSession = session;
                        } else if (session.isOffline() && session.getOfflineTime() + configurer.getOfflineCloseDelay() < now) {
                            LOG.info("移除下线超时的OfflineSession {}", session.getUserId());
                            session.close();
                            closeSession = session;
                        }
                        if (closeSession != null) {
                            this.sessionMap.remove(closeSession.getUserId(), closeSession);
                            closeSessions.add(closeSession);
                        }
                    } catch (Throwable e) {
                        LOG.error("clear {} invalided session exception", session.getUserId(), e);
                    }
                }
        );
        offlineSessionQueue.removeAll(closeSessions);
        int maxSize = configurer.getOfflineMaxSize();
        if (maxSize > 0) {
            int size = offlineSessionQueue.size() - maxSize;
            for (int i = 0; i < size; i++) {
                NetSession<UID> session = offlineSessionQueue.poll();
                if (session == null)
                    continue;
                try {
                    LOG.info("关闭第{}个 超过{}数量的OfflineSession {}", i, size, session.getUserId());
                    session.close();
                } catch (Throwable e) {
                    LOG.error("clear {} overmuch offline session exception", session.getUserId(), e);
                }
            }
        }
        debugSessionSize();
    }

    private void debugSessionSize() {
        int size = 0;
        int online = 0;
        for (NetSession<UID> session : this.sessionMap.values()) {
            size++;
            if (session.isOnline())
                online++;
        }
        LOG.info("会话管理器#{} Group -> 会话数量为 {} | 在线数 {}", this.getUserType(), size, online);
        LOG.info("会话管理器#{} Group -> 离线会话数量为 {} / {}", this.getUserType(), this.offlineSessionQueue.size(), this.configurer.getOfflineMaxSize());
    }

}
