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

	}

	@Override
	protected void onEndpointOnline(Session<UID> session) {
		this.offlineSessionQueue.remove(session);
	}

	@Override
	protected void onEndpointOffline(Session<UID> session) {
		if (session.isAuthenticated() && session.isOffline()) {
			this.offlineSessionQueue.add(as(session));
		}
	}

	@Override
	protected void onEndpointClose(Session<UID> session) {
		this.offlineSessionQueue.remove(session);
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
							session.closeWhen(EndpointStatus.OFFLINE);
							if (session.isClosed()) {
								closeSession = session;
							}
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
				NetSession<UID> session = this.offlineSessionQueue.poll();
				if (session == null) {
					continue;
				}
				try {
					boolean result = session.closeWhen(EndpointStatus.OFFLINE);
					LOG.info("关闭第{}个 超过{}数量的OfflineSession {} : 结果 {}", i, size, session.getUserId(), result);
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
