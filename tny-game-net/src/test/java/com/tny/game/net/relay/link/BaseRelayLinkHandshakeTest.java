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
package com.tny.game.net.relay.link;

import com.tny.game.net.application.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 客户端中继链路建立握手写出契约（fix-relay-static-link-open 任务 1.1，
 * specs relay-link"客户端中继链路建立握手不被未开状态吞没"）。
 * <p>
 * 缺陷复现：{@code auth()} 的 LINK_OPEN 首包在 status==INIT 时被 {@code canForward()}
 * 判丢（其 isActive 要求 OPEN，而 open() 要等 LINK_OPENED），静态集群链路结构性死锁。
 * 用例①钉"INIT+传输就绪首包必须抵达 transport"（先红）；
 * 用例②钉既有"终结早退路径载荷恰好释放一次"纪律不回退（基线绿）；
 * 用例③钉非握手包不得借道 INIT 放行（修复不得扩大豁免面）。
 */
class BaseRelayLinkHandshakeTest {

    @Mock
    private RelayTransport transport;

    @Mock
    private NetRelayServeInstance serveInstance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(serveInstance.serviceType()).thenReturn(mock(RpcServiceType.class));
        when(serveInstance.getService()).thenReturn("game-service");
        when(serveInstance.getId()).thenReturn(1L);
        when(transport.isActive()).thenReturn(true);
        when(transport.write(any(RelayPacket.class), any())).thenAnswer(invocation -> new MessageWriteFuture());
    }

    private CommonClientRelayLink newLink() {
        return new CommonClientRelayLink("test-key-1", serveInstance, transport);
    }

    @Test
    void linkOpenHandshakeReachesTransportWhileLinkNotYetOpen() {
        CommonClientRelayLink link = newLink();
        assertEquals(RelayLinkStatus.INIT, link.getStatus());

        link.auth(mock(RpcServiceType.class), "game-service", 1L);

        ArgumentCaptor<RelayPacket<?>> captor = ArgumentCaptor.forClass(RelayPacket.class);
        verify(transport).write(captor.capture(), any());
        assertEquals(RelayPacketType.LINK_OPEN, captor.getValue().getType());
    }

    @Test
    void packetsOnClosedLinkAreDroppedWithExactOnceRelease() {
        CommonClientRelayLink link = newLink();
        assertTrue(link.close());

        LinkOpenArguments arguments = mock(LinkOpenArguments.class);
        MessageWriteFuture future = link.write(LinkOpenPacket.FACTORY, arguments, true);

        verify(transport, never()).write(any(RelayPacket.class), any());
        verify(arguments, times(1)).release();
        assertThrows(Exception.class, () -> future.get(100, java.util.concurrent.TimeUnit.MILLISECONDS),
                "回执以断开异常完成");
    }

    @Test
    void handshakeAfterCloseIsStillDropped() {
        CommonClientRelayLink link = newLink();
        assertTrue(link.close());

        // 握手在途/终结竞态：已关闭链路的补发握手不得借道放行（豁免仅限 INIT）
        link.auth(mock(RpcServiceType.class), "game-service", 1L);

        verify(transport, never()).write(any(RelayPacket.class), any());
    }

    @Test
    void nonHandshakePacketsStillWaitForOpenState() {
        CommonClientRelayLink link = newLink();
        LinkVoidArguments arguments = mock(LinkVoidArguments.class);

        // INIT 且未开启时，非握手包（心跳）不得借道放行——维持"未开不可转发"边界
        link.write(LinkHeartBeatPacket.PING_FACTORY, arguments);

        verify(transport, never()).write(any(RelayPacket.class), any());
    }

}
