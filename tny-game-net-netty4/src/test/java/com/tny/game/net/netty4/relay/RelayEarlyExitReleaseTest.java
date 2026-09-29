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
package com.tny.game.net.netty4.relay;

import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.relay.packet.arguments.*;
import com.tny.game.net.session.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 13（Wave-B）红灯基线：中继早退路径载荷终结释放、丢弃留痕、
 * 链路未识别时协议错误处置不被观测日志 NPE 顶替、不可用通道丢弃消息体（relay-link/net-tunnel 契约）。
 */
class RelayEarlyExitReleaseTest {

    // ---------- (1) 已关闭链路提交中继：立即释放 + 失败回执 + 不写出 ----------

    @SuppressWarnings("unchecked")
    @Test
    void closedLinkRelayReleasesPayloadAndFailsReceipt() throws Exception {
        DefaultServerRelayExplorer explorer = new DefaultServerRelayExplorer();
        RelayTransport transport = mock(RelayTransport.class);
        AtomicBoolean active = new AtomicBoolean(true);
        when(transport.isActive()).thenAnswer(invocation -> active.get());
        explorer.acceptOpenLink(transport, mock(RpcServiceType.class), "svc", 1L, "k");

        Field linkMapField = DefaultServerRelayExplorer.class.getDeclaredField("linkMap");
        linkMapField.setAccessible(true);
        Map<String, ServerRelayLink> linkMap = (Map<String, ServerRelayLink>) linkMapField.get(explorer);
        NetRelayLink link = linkMap.values().iterator().next();
        link.close();

        reset(transport); // 清掉 open/close 阶段交互，只观察本用例的中继写出

        ByteBuf buf = Unpooled.buffer(4);
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        ByteBufMessageBody body = new ByteBufMessageBody(buf);
        CommonMessageHead head = new CommonMessageHead(5L, MessageMode.PUSH, 0, 1, 0, 0L, 1L, Collections.emptyMap());
        CommonMessage message = new CommonMessage(head, body);
        ServerRelayTunnel from = mock(ServerRelayTunnel.class);
        when(from.getInstanceId()).thenReturn(1L);
        when(from.getId()).thenReturn(2L);
        MessageWriteFuture awaiter = new MessageWriteFuture();

        MessageWriteFuture returned = link.relay(from, message, awaiter);

        assertEquals(0, buf.refCnt(), "已关闭链路的中继提交必须立即释放载荷（修复前拖到 GC/Cleaner）");
        assertTrue(awaiter.isCompletedExceptionally(), "写回执必须以失败完成");
        assertSame(awaiter, returned);
        verify(transport, never()).write(any(RelayPacket.class), any());
    }

    // ---------- (2) 未识别链路收 link 型包：协议错误断链，而非被 NPE 顶替 ----------

    @Test
    void unidentifiedLinkPacketClosesChannelNotNpeSwallow() {
        RelayPacketProcessor processor = new RelayPacketServerProcessor(mock(ServerRelayExplorer.class), mock(NetworkContext.class));
        NettyRelayPacketHandler handler = new NettyRelayPacketHandler(mock(NetBootstrapSetting.class), processor);
        EmbeddedChannel channel = new EmbeddedChannel(handler);

        // 分派器按包类型强转具体 packet 类，桩必须是真实类型
        TunnelSwitchLinkPacket packet = mock(TunnelSwitchLinkPacket.class);
        when(packet.getType()).thenReturn(RelayPacketType.TUNNEL_SWITCH_LINK);

        channel.writeInbound(packet);

        assertFalse(channel.isOpen(), "未识别链路的 link 型包必须按协议错误断链（修复前被 logReceive NPE 顶替为仅告警、连接保持）");
    }

    // ---------- (3) 不可用通道写出：消息体释放 + 回执失败 ----------

    @Test
    void writeOnUnavailableTunnelReleasesBody() {
        AtomicBoolean active = new AtomicBoolean(true);
        MessageTransport messageTransport = mock(MessageTransport.class);
        when(messageTransport.isActive()).thenAnswer(invocation -> active.get());
        TestTransportTunnel tunnel = new TestTransportTunnel(messageTransport);
        assertTrue(tunnel.open());

        ByteBuf buf = Unpooled.buffer(4);
        buf.writeBytes(new byte[]{5, 6, 7, 8});
        ByteBufMessageBody body = new ByteBufMessageBody(buf);
        CommonMessageHead head = new CommonMessageHead(6L, MessageMode.PUSH, 0, 1, 0, 0L, 1L, Collections.emptyMap());
        CommonMessage message = new CommonMessage(head, body);

        active.set(false); // 通道死亡（状态仍 OPEN 的竞态窗口由 transport.isActive 检出）
        MessageWriteFuture awaiter = new MessageWriteFuture();
        tunnel.write(message, awaiter);

        assertEquals(0, buf.refCnt(), "丢弃路径必须立即释放消息体（修复前静默泄漏给 GC）");
        assertTrue(awaiter.isCompletedExceptionally());
        verify(messageTransport, never()).write(any(Message.class), any());
    }

    /** 最小可测传输隧道：走 TransportTunnel 真实 checkAvailable 丢弃分支 */
    private static final class TestTransportTunnel extends TransportTunnel<NetSession, MessageTransport> {

        TestTransportTunnel(MessageTransport transport) {
            super(1L, transport, NetAccessMode.SERVER, mock(NetworkContext.class));
        }

        @Override
        protected boolean resetSession(NetSession newSession) {
            return true;
        }

        @Override
        protected boolean onOpen() {
            return true;
        }

        @Override
        protected void onOpened() {
        }

        @Override
        protected void onClose() {
        }

        @Override
        protected void onClosed() {
        }

        @Override
        protected void onDisconnected() {
        }

        @Override
        protected void doDisconnect() {
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return new InetSocketAddress(7200);
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return new InetSocketAddress(7201);
        }
    }

}
