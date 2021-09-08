package com.tny.game.net.netty4.network;

import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;

/**
 * 连接器 负责管理 socket 连接和重连
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/15 6:10 下午
 */
class TunnelConnector {

	public static final Logger LOGGER = LoggerFactory.getLogger(TunnelConnector.class);

	private int times;

	private final int retryTimes;

	private final long interval;

	private final NetTunnel<?> tunnel;

	private final Lock lock = new ReentrantLock();

	private final AtomicBoolean asyncReconnect = new AtomicBoolean(false);

	private final AtomicBoolean asyncConnect = new AtomicBoolean(false);

	TunnelConnector(NetTunnel<?> tunnel, int retryTimes, long interval) {
		this.times = 0;
		this.interval = interval;
		this.retryTimes = retryTimes;
		this.tunnel = tunnel;
	}

	public void connect(Executor executor) {
		if (this.tunnel.isActive()) {
			return;
		}
		if (executor != null) {
			if (this.asyncConnect.compareAndSet(false, true)) {
				executor.execute(() -> doConnect(true));
			}
		} else {
			this.doConnect(false);
		}
	}

	public void reconnect(ScheduledExecutorService executor) {
		if (this.tunnel.isActive()) {
			return;
		}
		if (executor != null) {
			if (this.asyncReconnect.compareAndSet(false, true)) {
				scheduleReconnect(executor, 0);
			}
		} else {
			this.doReconnect(null);
		}
	}

	private void scheduleReconnect(ScheduledExecutorService executor, long delay) {
		if (delay > 0) {
			executor.schedule(() -> this.doReconnect(executor), delay, TimeUnit.MILLISECONDS);
		} else {
			executor.execute(() -> this.doReconnect(executor));
		}
	}

	private void doConnect(boolean async) {
		if (this.tunnel.isActive()) {
			return;
		}
		this.lock.lock();
		try {
			openTunnel();
		} finally {
			if (async) {
				this.asyncConnect.set(false);
			}
			this.lock.unlock();
		}
	}

	private void doReconnect(ScheduledExecutorService executor) {
		if (this.tunnel.isActive()) {
			return;
		}
		boolean stop = false;
		try {
			while (this.asyncReconnect.get() && this.times < this.retryTimes) {
				this.lock.lock();
				try {
					LOGGER.info("{} reconnect {} times", this.tunnel, this.times);
					if (openTunnel()) { // 成功停止
						LOGGER.info("{} reconnect {} times success", this.tunnel, this.times);
						this.times = 0;
						stop = true;
						return;
					} else {            // 失败
						LOGGER.info("{} reconnect {} times failed", this.tunnel, this.times);
						this.times++;
						if (this.times < this.retryTimes) { // 继续重试
							if (executor != null) { // 异步提交, 否则继续循环
								this.scheduleReconnect(executor, this.interval);
							}
						} else { // 尝试次数满停止
							LOGGER.warn("reconnect {} failed times {} >= {}, stop reconnect", this.tunnel, this.times, this.retryTimes);
							this.times = 0;
							stop = true;
						}
					}
				} finally {
					this.lock.unlock();
				}
			}
		} finally {
			if (executor != null && stop) { // 如果是异步成功 重置asyncReconnect状态
				this.asyncReconnect.set(false);
			}
		}
	}

	private boolean openTunnel() {
		if (this.tunnel.isActive()) {
			return true;
		}
		LOGGER.info("{} is ready to submit reconnect {} task for the {} time ", this, this.tunnel, this.times);
		if (!this.tunnel.isActive() && this.tunnel.getStatus() != TunnelStatus.INIT) {
			LOGGER.info("{} submit reconnect {} task for the {} time", this, this.tunnel, this.times);
			this.tunnel.reset();
		}
		LOGGER.info("try connect {}", this);
		return this.tunnel.open();
	}

}
