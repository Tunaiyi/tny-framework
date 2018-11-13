package com.tny.game.net.endpoint;

import com.tny.game.common.concurrent.CoreThreadFactory;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.endpoint.listener.SessionEventBuses;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class AbstractSessionKeeper<UID> extends AbstractEndpointKeeper<UID, Session<UID>, NetSession<UID>> implements SessionKeeper<UID> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    protected SessionKeeperSetting setting;

    /* 离线session */
    protected final Queue<NetSession<UID>> offlineSessionQueue = new ConcurrentLinkedQueue<>();

    public AbstractSessionKeeper(String userType, SessionKeeperSetting setting) {
        super(userType);
        this.setting = setting;
        ScheduledExecutorService sessionScanExecutor = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionScanWorker", true));
        sessionScanExecutor.scheduleAtFixedRate(this::clearInvalidedSession, setting.getClearInterval(), setting.getClearInterval(), TimeUnit.MILLISECONDS);
        SessionEventBuses buses = SessionEventBuses.buses();
        buses.onlineEvent().add(this::onOnline);
        buses.offlineEvent().add(this::onOffline);
        buses.onlineEvent().add(this::onOnline);
        buses.closeEvent().add(this::onClose);
    }

    @Override
    public boolean isOnline(UID userId) {
        Session<UID> session = this.getEndpoint(userId);
        return session != null && session.isOnline();
    }

    @Override
    public int onlineSize() {
        int online = 0;
        for (NetSession<UID> session : this.endpointMap.values()) {
            if (session.isOnline())
                online++;
        }
        return online;
    }

    @Override
    public Session<UID> offline(UID userId) {
        NetSession<UID> session = this.endpointMap.get(userId);
        if (session != null)
            session.offline();
        return session;
    }

    @Override
    public void offlineAll() {
        this.endpointMap.forEach((key, session) -> session.offline());
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
        this.endpointMap.remove(netSession.getUserId(), netSession);
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
                            LOG.info("移除已关闭的 OfflineSession userId : {}", session.getUserId());
                            closeSession = session;
                        } else if (session.isOffline() && session.getOfflineTime() + setting.getOfflineCloseDelay() < now) {
                            LOG.info("移除下线超时的 OfflineSession userId : {}", session.getUserId());
                            session.close();
                            closeSession = session;
                        }
                        if (closeSession != null) {
                            this.endpointMap.remove(closeSession.getUserId(), closeSession);
                            closeSessions.add(closeSession);
                        }
                    } catch (Throwable e) {
                        LOG.error("clear {} invalided session exception", session.getUserId(), e);
                    }
                }
        );
        offlineSessionQueue.removeAll(closeSessions);
        int maxSize = setting.getOfflineMaxSize();
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

    protected void debugSessionSize() {
        int size = 0;
        int online = 0;
        for (NetSession<UID> session : this.endpointMap.values()) {
            size++;
            if (session.isOnline())
                online++;
        }
        LOG.info("会话管理器#{} Group -> 会话数量为 {} | 在线数 {}", this.getUserType(), size, online);
        LOG.info("会话管理器#{} Group -> 离线会话数量为 {} / {}", this.getUserType(), this.offlineSessionQueue.size(), this.setting.getOfflineMaxSize());
    }

}
