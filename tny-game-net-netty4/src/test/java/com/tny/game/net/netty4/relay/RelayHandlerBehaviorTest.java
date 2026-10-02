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
package com.tny.game.net.netty4.relay;

import com.tny.game.common.exception.*;
import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

/**
 * 中继包处理器行为（relay-link 规格 · 事件传播正确性 / 失败终结释放）。
 */
class RelayHandlerBehaviorTest {

    RelayPacketProcessor processor;
    NettyRelayPacketHandler handler;
    EmbeddedChannel channel;
    ChannelHandlerContext ctx;
    NetRelayLink link;

    @BeforeEach
    void setUp() {
        processor = mock(RelayPacketProcessor.class);
        handler = new NettyRelayPacketHandler(mock(NetBootstrapSetting.class), processor);
        channel = new EmbeddedChannel();
        link = mock(NetRelayLink.class);
        when(link.getService()).thenReturn("fixture-service"); // ofRelay 动态建组，null 键会 NPE
        channel.attr(NettyRelayAttrKeys.RELAY_LINK).set(link);
        ctx = mock(ChannelHandlerContext.class, RETURNS_DEEP_STUBS);
        when(ctx.channel()).thenReturn(channel);
    }

    /** 激活事件必须按语义传播 channelActive（当前实现误广播 registered 或被日志吞掉，应红） */
    @Test
    void channelActivePropagatesActiveEvent() throws Exception {
        handler.channelActive(ctx);
        verify(ctx, times(1)).fireChannelActive();
        verify(ctx, never()).fireChannelRegistered();
    }

    /** 非中继包对象写出必须穿透下游而非静默吞弃（当前零调用致 promise 悬挂，应红） */
    @Test
    void writePassesThroughUnknownObject() throws Exception {
        Object plainObject = new Object();
        ChannelPromise promise = channel.newPromise();
        handler.write(ctx, plainObject, promise);
        verify(ctx, times(1)).write(plainObject, promise);
    }

    /** 校验拒绝型异常（携带结果码）也必须终结释放包体缓冲（当前分支不释放，应红） */
    @Test
    void verificationRejectionReleasesPacket() throws Exception {
        TunnelRelayPacket packet = relayPacket();
        doThrow(new ResultCodeRuntimeException(NetResultCode.NO_LOGIN))
                .when(processor).onTunnelRelay(any(), any());

        handler.channelRead(ctx, packet);

        verify(processor, times(1)).onTunnelRelay(any(), any()); // 路径证明：异常必须来自处理器消费点
        verify(packet.getArguments(), times(1)).release();
    }

    /** 未知异常释放（现状 Throwable 分支已正确，防回归绿基线） */
    @Test
    void unknownExceptionReleasesPacket() throws Exception {
        TunnelRelayPacket packet = relayPacket();
        doThrow(new RuntimeException("fixture failure"))
                .when(processor).onTunnelRelay(any(), any());

        handler.channelRead(ctx, packet);

        verify(processor, times(1)).onTunnelRelay(any(), any()); // 路径证明
        verify(packet.getArguments(), times(1)).release();
    }

    private TunnelRelayPacket relayPacket() {
        TunnelRelayPacket packet = mock(TunnelRelayPacket.class);
        when(packet.getType()).thenReturn(RelayPacketType.TUNNEL_RELAY);
        when(packet.getArguments()).thenReturn(mock(TunnelRelayArguments.class));
        return packet;
    }

}
