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
import com.tny.game.common.url.*;
import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 7（Wave-A）红灯基线：重连调度不熄火（net-tunnel"连接超时与重连调度"契约）。
 * 复现三条熄火路径：同步失败竞态（retryFuture 未清即判"已有任务"）、断开后首败即弃、
 * URL 重连参数解析异常打断调度链。
 */
class TunnelConnectorReconnectTest {

    private ScheduledExecutorService executor;

    private ClientGuide guide;

    private ClientConnectorSetting connectorSetting;

    @BeforeEach
    void setUp() {
        executor = Executors.newSingleThreadScheduledExecutor();
        guide = mock(ClientGuide.class);
        ClientBootstrapSetting bootstrapSetting = mock(ClientBootstrapSetting.class);
        connectorSetting = mock(ClientConnectorSetting.class);
        when(bootstrapSetting.getConnector()).thenReturn(connectorSetting);
        when(guide.getSetting()).thenReturn(bootstrapSetting);
        when(connectorSetting.isAutoReconnect()).thenReturn(true);
        when(connectorSetting.getRetryIntervals()).thenReturn(java.util.List.of(10L));
    }

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    private static CompleteStageFuture<NetTunnel> failedFuture() {
        CompleteStageFuture<NetTunnel> future = new CompleteStageFuture<>();
        future.completeExceptionally(new TunnelConnectException("stub connect refused"));
        return future;
    }

    /** 首败→再试：同步失败竞态下重试链必须持续武装（修复前 autoReconnect.finally 清 future 的时序导致熄火） */
    @Test
    void synchronousConnectFailureKeepsRetryChainArmed() throws Exception {
        when(connectorSetting.getRetryTimes()).thenReturn(6);
        AtomicInteger attempts = new AtomicInteger();
        URL url = URL.valueOf("127.0.0.1:7099");
        when(guide.connectAsync(eq(url), any())).thenAnswer(invocation -> {
            attempts.incrementAndGet();
            return failedFuture();
        });
        CommonTunnelConnector connector = new CommonTunnelConnector(guide, url, null, executor);
        try {
            connector.open();
            Thread.sleep(600); // 10ms 间隔 + 裕量：6 次上限内应大量尝试
            assertTrue(attempts.get() >= 4,
                    "重试链持续武装时 600ms 内至少 4 次尝试；实际 " + attempts.get() + "（修复前停在 2 即熄火）");
        } finally {
            connector.close();
        }
    }

    /** 建立后断开：重连首败不得放弃（修复前 reconnect 走 retry=false 单次即弃） */
    @Test
    void afterEstablishedDisconnectRetryContinues() throws Exception {
        when(connectorSetting.getRetryTimes()).thenReturn(6);
        AtomicInteger attempts = new AtomicInteger();
        NetTunnel established = mock(NetTunnel.class);
        when(established.getSession()).thenReturn(mock(NetSession.class));
        CompleteStageFuture<NetTunnel> ok = new CompleteStageFuture<>();
        ok.complete(established);
        URL url = URL.valueOf("127.0.0.1:7099");
        when(guide.connectAsync(eq(url), any())).thenAnswer(invocation -> {
            int n = attempts.incrementAndGet();
            return n == 1 ? ok : failedFuture();
        });
        CommonTunnelConnector connector = new CommonTunnelConnector(guide, url, null, executor);
        try {
            connector.open();
            assertSame(TunnelConnectorStatus.CONNECTED, connector.status());
            connector.onUnavailable(established); // 模拟断开
            Thread.sleep(600);
            assertTrue(attempts.get() >= 3,
                    "断开后重连持续尝试；实际 " + attempts.get() + "（修复前 2 即弃：首败不续排）");
        } finally {
            connector.close();
        }
    }

    /** URL 重连间隔参数非法：回退 setting 值，调度链不得被解析异常打断 */
    @Test
    void malformedUrlRetryParamFallsBack() throws Exception {
        when(connectorSetting.getRetryTimes()).thenReturn(6);
        AtomicInteger attempts = new AtomicInteger();
        URL url = URL.valueOf("127.0.0.1:7099?retry_intervals=abc");
        when(guide.connectAsync(eq(url), any())).thenAnswer(invocation -> {
            attempts.incrementAndGet();
            return failedFuture();
        });
        CommonTunnelConnector connector = new CommonTunnelConnector(guide, url, null, executor);
        try {
            assertDoesNotThrow(connector::open);
            Thread.sleep(400);
            assertTrue(attempts.get() >= 2,
                    "非法 URL 参数不得打断重连调度（回退配置值）；实际 " + attempts.get());
        } finally {
            connector.close();
        }
    }

    /** 兼容锚：达到最大重试次数后停止调度 */
    @Test
    void retryTimesCapStopsScheduling() throws Exception {
        when(connectorSetting.getRetryTimes()).thenReturn(2);
        AtomicInteger attempts = new AtomicInteger();
        URL url = URL.valueOf("127.0.0.1:7099");
        when(guide.connectAsync(eq(url), any())).thenAnswer(invocation -> {
            attempts.incrementAndGet();
            return failedFuture();
        });
        CommonTunnelConnector connector = new CommonTunnelConnector(guide, url, null, executor);
        try {
            connector.open();
            Thread.sleep(600);
            assertTrue(attempts.get() <= 4,
                    "上限 2 次重试后必须停：实际 " + attempts.get());
        } finally {
            connector.close();
        }
    }

}
