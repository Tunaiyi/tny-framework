/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private final AtomicReference<ScheduledFuture<?>> retryFuture = new AtomicReference<>();

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


    private void resetScheduleReconnect() {
        ScheduledFuture<?> future = retryFuture.getAndSet(null);
        if (future != null) {
            future.cancel(true);
        }
        autoRetry.set(false);
        autoRetryTimes.set(0);
    }

    private void handleConnectFailed(TunnelConnectorStatus expected, boolean retry) {
        if (!status.compareAndSet(expected.id(), TunnelConnectorStatus.DISCONNECT.id())) {
            return;
        }
        // 本次尝试结束：先释放调度锁再决定续排，同步失败竞态不再让链条熄火；
        // 续排与否统一由 setting.isAutoReconnect()（scheduleReconnect 内检）决定（net-tunnel 契约）
        autoRetry.set(false);
        scheduleReconnect();
    }

    @Override
    public void reconnect() {
        this.doReconnect(true);
    }

    private void doReconnect(boolean retry) {
        this.connect(TunnelConnectorStatus.DISCONNECT, TunnelConnectorStatus.RECONNECTING, retry)
                .whenComplete((result, cause) -> {
                    if (result != null) {
                        resetScheduleReconnect();
                    }
                });
    }

    private void autoReconnect() {
        // 本周期任务已在执行：先清句柄与调度锁，失败回调才能重新武装下一周期
        retryFuture.set(null);
        autoRetry.set(false);
        doReconnect(true);
    }

    private void scheduleReconnect() {
        if (this.isClosed()) {
            return;
        }
        if (!setting.isAutoReconnect()) {
            return;
        }
        var maxRetryTimes = getMaxRetryTimes();
        var retryTimes = autoRetryTimes.get();
        if (maxRetryTimes > 0 && retryTimes >= maxRetryTimes) { // 继续重试
            autoRetryTimes.set(0);
            return;
        }
        if (maxRetryTimes <= 0 && retryTimes > 0 && retryTimes % 10 == 0) {
            // 无上限重试的周期性留痕（net-tunnel"无上限重试"契约）
            LOGGER.info("auto reconnect to {} still retrying, times {}", url, retryTimes);
        }
        if (this.autoRetry.compareAndSet(false, true)) {
            var armedTimes = autoRetryTimes.getAndIncrement();
            try {
                retryFuture.set(executor.schedule(this::autoReconnect, getInterval(armedTimes), TimeUnit.MILLISECONDS));
            } catch (Throwable e) {
                autoRetry.set(false);
                LOGGER.error("schedule reconnect to {} failed", url, e);
            }
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
        try {
            String[] data = StringUtils.split(intervals, ",");
            return Stream.of(data).map(Long::parseLong).filter(i -> i > 0).collect(Collectors.toList());
        } catch (RuntimeException e) {
            // URL 参数非法不得打断重连调度链（net-tunnel 契约），回退配置值并留痕
            LOGGER.warn("url {} retry_intervals '{}' invalid, fallback to setting: {}", url, intervals, e.getMessage());
            return setting.getRetryIntervals();
        }
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
