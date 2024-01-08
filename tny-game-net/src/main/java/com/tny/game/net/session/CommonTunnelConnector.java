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
import com.tny.game.common.enums.*;
import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.transport.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

import static com.tny.game.net.utils.NetConfigs.*;

/**
 * 连接器 负责管理 socket 连接和重连
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/15 6:10 下午
 */
public class CommonTunnelConnector implements TunnelConnector, TunnelUnavailableWatch {

    public static final Logger LOGGER = LoggerFactory.getLogger(CommonTunnelConnector.class);

    private final ScheduledExecutorService executor;

    private final URL url;

    private final ClientGuide guide;

    private final PostConnect postConnect;

    private final ClientConnectorSetting setting;

    private volatile ScheduledFuture<?> retryFuture;

    private final AtomicBoolean autoRetry = new AtomicBoolean(false);

    private final AtomicInteger autoRetryTimes = new AtomicInteger();

    private final AtomicInteger status = new AtomicInteger(TunnelConnectorStatus.INITIAL.id());

    public CommonTunnelConnector(ClientGuide guide, URL url, PostConnect postConnect, ScheduledExecutorService executor) {
        this.url = url;
        this.postConnect = postConnect;
        this.executor = executor;
        this.guide = guide;
        this.setting = guide.getSetting().getConnector();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public CompletionStageFuture<@Nullable Session> open() {
        return connect(TunnelConnectorStatus.INITIAL, TunnelConnectorStatus.CONNECTING, setting.isAutoReconnect());
    }

    private CompleteStageFuture<@Nullable Session> connect(TunnelConnectorStatus expectedValue, TunnelConnectorStatus newValue, boolean retry) {
        if (status.compareAndSet(expectedValue.id(), newValue.id())) {
            var future = new CompleteStageFuture<Session>();
            var connectFuture = guide.connectAsync(url, this);
            connectFuture.whenComplete((tunnel, throwable) -> {
                if (throwable != null) {
                    future.completeExceptionally(throwable);
                    handleConnectFailed(newValue, retry);
                    return;
                }
                try {
                    postConnect(tunnel).whenComplete((result, cause) -> {
                        if (Booleans.isTrue(result) && status.compareAndSet(newValue.id(), TunnelConnectorStatus.CONNECTED.id())) {
                            future.complete(tunnel.getSession());
                            return;
                        }
                        try {
                            tunnel.close();
                        } finally {
                            if (cause != null) {
                                future.completeExceptionally(cause);
                            } else {
                                future.complete(null);
                            }
                            handleConnectFailed(newValue, retry);
                        }
                    });
                } catch (Throwable e) {
                    tunnel.close();
                    LOGGER.error("post connect error", e);
                    future.completeExceptionally(e);
                    handleConnectFailed(newValue, retry);
                }
            });
            return future;
        } else {
            return CompleteStageFuture.result(null);
        }
    }


    private void stopReconnect() {
        retryFuture = null;
    }

    private void resetScheduleReconnect() {
        if (retryFuture != null) {
            retryFuture.cancel(true);
            retryFuture = null;
        }
        autoRetryTimes.set(0);
    }

    private void handleConnectFailed(TunnelConnectorStatus expected, boolean retry) {
        if (!status.compareAndSet(expected.id(), TunnelConnectorStatus.DISCONNECT.id())) {
            return;
        }
        if (!retry) {
            return;
        }
        scheduleReconnect();
    }

    @Override
    public void reconnect() {
        this.doReconnect(false);
    }

    private void doReconnect(boolean retry) {
        this.connect(TunnelConnectorStatus.DISCONNECT, TunnelConnectorStatus.RECONNECTING, retry)
                .whenComplete((result, cause) -> {
                    if (result != null) {
                        resetScheduleReconnect();
                        return;
                    }
                    if (retry) {
                        autoRetry.set(false);
                    }
                });
    }

    private void autoReconnect() {
        try {
            doReconnect(true);
        } finally {
            stopReconnect();
        }
    }

    private void scheduleReconnect() {
        if (this.isClosed()) {
            return;
        }
        if (!setting.isAutoReconnect()) {
            return;
        }
        var maxRetryTimes = getMaxRetryTimes();// 失败
        var retryTimes = autoRetryTimes.get();
        if (maxRetryTimes > 0 && retryTimes >= maxRetryTimes) { // 继续重试
            autoRetryTimes.set(0);
            return;
        }
        if (retryFuture == null && this.autoRetry.compareAndSet(false, true)) {
            retryTimes = autoRetryTimes.getAndIncrement();
            retryFuture = executor.schedule(this::autoReconnect, getInterval(retryTimes), TimeUnit.MILLISECONDS);
        }
    }

    private CompletionStageFuture<Boolean> postConnect(NetTunnel tunnel) {
        if (this.postConnect == null) {
            return CompleteStageFuture.result(true);
        }
        return this.postConnect.onConnected(tunnel);
    }

    private long getInterval(int times) {
        List<Long> intervals = getConnectRetryIntervals();
        if (CollectionUtils.isEmpty(intervals)) {
            return RETRY_INTERVAL_DEFAULT_VALUE;
        }
        if (times >= intervals.size()) {
            return intervals.get(intervals.size() - 1);
        }
        return intervals.get(times);
    }


    private List<Long> getConnectRetryIntervals() {
        String intervals = this.url.getParameter(RETRY_INTERVAL_URL_PARAM, "");
        if (StringUtils.isEmpty(intervals)) {
            return setting.getRetryIntervals();
        }
        String[] data = StringUtils.split(intervals, ",");
        return Stream.of(data).map(Long::parseLong).filter(i -> i > 0).collect(Collectors.toList());
    }

    private int getMaxRetryTimes() {
        ClientConnectorSetting setting = guide.getSetting().getConnector();
        return this.url.getParameter(RETRY_TIMES_URL_PARAM, setting.getRetryTimes());
    }

    @Override
    public void close() {
        while (true) {
            var value = this.status.get();
            if (value == TunnelConnectorStatus.CLOSE.id()) {
                return;
            }
            if (this.status.compareAndSet(value, TunnelConnectorStatus.CLOSE.id())) {
                this.resetScheduleReconnect();
                return;
            }
        }
    }

    @Override
    public void onUnavailable(NetTunnel tunnel) {
        if (status.compareAndSet(TunnelConnectorStatus.CONNECTED.id(), TunnelConnectorStatus.DISCONNECT.id())) {
            reconnect();
        }
    }

    @Override
    public TunnelConnectorStatus status() {
        return EnumAide.of(TunnelConnectorStatus.class, this.status.get());
    }


    private boolean isClosed() {
        return status() == TunnelConnectorStatus.CLOSE;
    }

}
