/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.session;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.application.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class AutoCloseableSessionKeeper extends AbstractSessionKeeper {

    private static final ScheduledExecutorService sessionScanExecutor = Executors.newSingleThreadScheduledExecutor(
            new CoreThreadFactory("SessionScanWorker", true));

    private static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

    protected SessionKeeperSetting setting;

    /* 离线session */
    private final Queue<NetSession> offlineSessionQueue = new ConcurrentLinkedQueue<>();


    public AutoCloseableSessionKeeper(ContactType contactType, SessionKeeperSetting setting) {
        super(contactType);
        this.setting = setting;
        if (isDelayClose()) {
            sessionScanExecutor.scheduleAtFixedRate(this::clearInvalidedSession,
                    setting.getClearInterval(), setting.getClearInterval(), TimeUnit.MILLISECONDS);
        }

    }

    @Override
    protected void onOnline(Session session) {
        this.offlineSessionQueue.remove((NetSession) session);
    }

    private boolean isDelayClose() {
        return this.setting.getOfflineCloseDelay() > 0;
    }

    @Override
    protected void onOffline(Session session) {
        if (!session.isAuthenticated() || !session.isOffline()) {
            return;
        }
        if (isDelayClose()) {
            this.offlineSessionQueue.add(as(session));
        }
    }

    @Override
    protected void onClose(Session session) {
        this.offlineSessionQueue.remove((NetSession) session);
    }

    /**
     * 清楚失效 Session
     */
    private void clearInvalidedSession() {
        long now = System.currentTimeMillis();
        // int size = this.offlineSessions.size() - offlineSessionMaxSize;
        Set<NetSession> closeSessions = new HashSet<>();
        this.offlineSessionQueue.forEach(session -> {
                    try {
                        NetSession closeSession = null;
                        if (session.isClosed()) {
                            LOG.info("移除已关闭的 OfflineSession identify : {}", session.getIdentify());
                            closeSession = session;
                        } else if (session.isOffline() && session.getOfflineTime() + this.setting.getOfflineCloseDelay() < now) {
                            LOG.info("移除下线超时的 OfflineSession identify : {}", session.getIdentify());
                            session.closeWhen(SessionStatus.OFFLINE);
                            if (session.isClosed()) {
                                closeSession = session;
                            }
                        }
                        if (closeSession != null) {
                            this.removeSession(closeSession.getIdentify(), closeSession);
                            closeSessions.add(closeSession);
                        }
                    } catch (Throwable e) {
                        LOG.error("clear {} invalided session exception", session.getIdentify(), e);
                    }
                }
        );
        this.offlineSessionQueue.removeAll(closeSessions);
        int maxSize = this.setting.getOfflineMaxSize();
        if (maxSize > 0) {
            int size = this.offlineSessionQueue.size() - maxSize;
            for (int i = 0; i < size; i++) {
                NetSession session = this.offlineSessionQueue.poll();
                if (session == null) {
                    continue;
                }
                try {
                    boolean result = session.closeWhen(SessionStatus.OFFLINE);
                    LOG.info("关闭第{}个 超过{}数量的OfflineSession {} : 结果 {}", i, size, session.getIdentify(), result);
                } catch (Throwable e) {
                    LOG.error("clear {} overmuch offline session exception", session.getIdentify(), e);
                }
            }
        }
        monitorSession();
    }

    @Override
    protected void monitorSession() {
        super.monitorSession();
        LOG.info("会话管理器#{} Group -> 离线会话数量为 {} / {}", this.getContactType(), this.offlineSessionQueue.size(),
                this.setting.getOfflineMaxSize());
    }

}
