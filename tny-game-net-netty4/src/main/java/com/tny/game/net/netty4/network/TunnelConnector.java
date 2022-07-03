package com.tny.game.net.netty4.network;

import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.utils.NetConfigs.*;

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

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final NetTunnel<?> tunnel;

    private final Lock lock = new ReentrantLock();

    private final TunnelConnectExecutor executor;

    private final AtomicBoolean asyncConnect = new AtomicBoolean(false);

    private final AtomicBoolean autoRetrying = new AtomicBoolean(false);

    private final NettyClient<?> client;

    private volatile ScheduledFuture<Void> future;

    TunnelConnector(NetTunnel<?> tunnel, NettyClient<?> client, TunnelConnectExecutor executor) {
        this.times = 0;
        this.executor = executor;
        this.client = client;
        this.tunnel = tunnel;
    }

    public void connect(ConnectCallback callback) {
        callback = ifNull(callback, ConnectCallback.NOOP);
        if (this.tunnel.isActive()) {
            callback.onConnect(ConnectCallbackStatus.CONNECTED, this.tunnel, null);
        }
        if (this.asyncConnect.compareAndSet(false, true)) {
            ConnectCallback cb = callback;
            executor.execute(() -> doConnect(cb));
        } else {
            callback.onConnect(ConnectCallbackStatus.CONNECTING, this.tunnel, new ConnectingException("connecting"));
        }
    }

    public void reconnect() {
        if (!this.client.isAutoReconnect()) {
            return;
        }
        if (executor == null) {
            return;
        }
        if (this.tunnel.isActive()) {
            return;
        }
        if (this.autoRetrying.compareAndSet(false, true)) {
            this.times = 0;
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        if (this.checkNotReconnect()) {
            return;
        }
        future = executor.schedule(this::doReconnect, getInterval(this.times), TimeUnit.MILLISECONDS);
    }

    private long getInterval(int times) {
        List<Long> intervals = this.client.getConnectRetryIntervals();
        if (CollectionUtils.isEmpty(intervals)) {
            return RETRY_INTERVAL_DEFAULT_VALUE;
        }
        if (times >= intervals.size()) {
            return intervals.get(intervals.size() - 1);
        }
        return intervals.get(times);
    }

    private void doConnect(ConnectCallback callback) {
        try {
            if (this.tunnel.isActive()) {
                callback.onConnect(ConnectCallbackStatus.CONNECTED, this.tunnel, null);
                return;
            }
            this.lock.lock();
            try {
                if (openTunnel()) {
                    callback.onConnect(ConnectCallbackStatus.CONNECTED, this.tunnel, null);
                } else {
                    callback.onConnect(ConnectCallbackStatus.EXCEPTION, this.tunnel, new ConnectFailedException("connect failed"));
                    if (this.client.isAutoReconnect()) {
                        reconnect();
                    }
                }
            } catch (Throwable e) {
                callback.onConnect(ConnectCallbackStatus.EXCEPTION, this.tunnel, e);
            } finally {
                this.lock.unlock();
            }
        } finally {
            this.asyncConnect.set(false);
        }
    }

    private boolean checkNotReconnect() {
        return this.shutdown.get() || this.tunnel.isClosed() || this.tunnel.isActive();
    }

    private void doReconnect() {
        boolean stop = false;
        try {
            if (checkNotReconnect()) {
                stop = true;
                return;
            }
            this.lock.lock();
            try {
                if (checkNotReconnect()) {
                    stop = true;
                    return;
                }
                int retryTimes = client.getConnectRetryTimes();// 失败
                LOGGER.info("{} reconnect {} times", this.tunnel, this.times);
                if (openTunnel()) { // 成功停止
                    LOGGER.info("{} reconnect {} times success", this.tunnel, this.times);
                    future = null;
                    stop = true;
                } else {
                    if (retryTimes > 0 && this.times >= retryTimes) { // 继续重试
                        // 尝试次数满停止
                        LOGGER.warn("reconnect {} failed times {} >= {}, stop reconnect", this.tunnel, this.times, retryTimes);
                        stop = true;
                    } else {
                        LOGGER.info("{} reconnect {} times failed", this.tunnel, this.times);
                    }
                }
            } catch (Throwable e) {
                LOGGER.info("{} reconnect {} times exception", this.tunnel, this.times, e);
            } finally {
                this.times++;
                this.lock.unlock();
            }
        } finally {
            if (stop) {
                this.times = 0;
                autoRetrying.set(false);
            } else {
                this.scheduleReconnect();
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

    public void shutdown() {
        if (this.shutdown.compareAndSet(false, true)) {
            ScheduledFuture<Void> future = this.future;
            if (future != null) {
                future.cancel(false);
            }
        }
    }

}
