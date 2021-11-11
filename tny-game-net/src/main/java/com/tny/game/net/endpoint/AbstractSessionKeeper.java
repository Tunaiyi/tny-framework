package com.tny.game.net.endpoint;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.base.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class AbstractSessionKeeper<UID> extends AbstractEndpointKeeper<UID, Session<UID>, NetSession<UID>> {

	private static final Logger LOG = LoggerFactory.getLogger(NetLogger.SESSION);

	protected SessionKeeperSetting setting;

	/* 离线session */
	private final Queue<NetSession<UID>> offlineSessionQueue = new ConcurrentLinkedQueue<>();

	protected SessionFactory<UID, NetSession<UID>, SessionSetting> factory;

	public AbstractSessionKeeper(String userType, SessionFactory<UID, ? extends NetSession<UID>, ? extends SessionSetting> factory,
			SessionKeeperSetting setting) {
		super(userType);
		this.setting = setting;
		this.factory = as(factory);
		ScheduledExecutorService sessionScanExecutor = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionScanWorker", true));
		sessionScanExecutor
				.scheduleAtFixedRate(this::clearInvalidedSession, setting.getClearInterval(), setting.getClearInterval(), TimeUnit.MILLISECONDS);
		EndpointEventBuses buses = EndpointEventBuses.buses();
		buses.onlineEvent().add(this::onOnline);
		buses.offlineEvent().add(this::onOffline);
		buses.onlineEvent().add(this::onOnline);
		buses.closeEvent().add(this::onClose);
	}

	private void onOnline(Endpoint<?> session) {
		if (!this.getUserType().equals(session.getUserType())) {
			return;
		}
		NetSession<UID> netSession = as(session);
		this.offlineSessionQueue.remove(netSession);
	}

	private void onOffline(Endpoint<?> session) {
		if (!this.getUserType().equals(session.getUserType())) {
			return;
		}
		if (session.isLogin() && session.isOffline()) {
			this.offlineSessionQueue.add(as(session));
		}
	}

	private void onClose(Endpoint<?> session) {
		if (!this.getUserType().equals(session.getUserType())) {
			return;
		}
		if (!session.isLogin()) {
			return;
		}
		NetSession<UID> netSession = as(session);
		this.removeEndpoint(netSession.getUserId(), netSession);
		this.offlineSessionQueue.remove(netSession);
	}

	/**
	 * 清楚失效 Session
	 */
	private void clearInvalidedSession() {
		long now = System.currentTimeMillis();
		// int size = this.offlineSessions.size() - offlineSessionMaxSize;
		Set<NetSession<UID>> closeSessions = new HashSet<>();
		this.offlineSessionQueue.forEach(session -> {
					try {
						NetSession<UID> closeSession = null;
						if (session.isClosed()) {
							LOG.info("移除已关闭的 OfflineSession userId : {}", session.getUserId());
							closeSession = session;
						} else if (session.isOffline() && session.getOfflineTime() + this.setting.getOfflineCloseDelay() < now) {
							LOG.info("移除下线超时的 OfflineSession userId : {}", session.getUserId());
							session.close();
							closeSession = session;
						}
						if (closeSession != null) {
							this.removeEndpoint(closeSession.getUserId(), closeSession);
							closeSessions.add(closeSession);
						}
					} catch (Throwable e) {
						LOG.error("clear {} invalided session exception", session.getUserId(), e);
					}
				}
		);
		this.offlineSessionQueue.removeAll(closeSessions);
		int maxSize = this.setting.getOfflineMaxSize();
		if (maxSize > 0) {
			int size = this.offlineSessionQueue.size() - maxSize;
			for (int i = 0; i < size; i++) {
				Session<UID> session = this.offlineSessionQueue.poll();
				if (session == null) {
					continue;
				}
				try {
					LOG.info("关闭第{}个 超过{}数量的OfflineSession {}", i, size, session.getUserId());
					session.close();
				} catch (Throwable e) {
					LOG.error("clear {} overmuch offline session exception", session.getUserId(), e);
				}
			}
		}
		monitorEndpoint();
	}

	@Override
	protected void monitorEndpoint() {
		super.monitorEndpoint();
		LOG.info("会话管理器#{} Group -> 离线会话数量为 {} / {}", this.getUserType(), this.offlineSessionQueue.size(), this.setting.getOfflineMaxSize());
	}

}
