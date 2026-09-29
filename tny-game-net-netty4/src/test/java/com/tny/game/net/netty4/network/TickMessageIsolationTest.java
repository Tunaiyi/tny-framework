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
package com.tny.game.net.netty4.network;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 27（Wave-C）红灯基线：心跳（跨连接共享单例）处理不得进入头清理可变面
 * （net-protocol"心跳报文处理不产生共享状态变更"）。
 */
class TickMessageIsolationTest {

    private EmbeddedChannel channelWith(RpcMonitor monitor) {
        NetBootstrapSetting bootstrapSetting = mock(NetBootstrapSetting.class);
        when(bootstrapSetting.getReadIgnoreHeaders()).thenReturn(Set.of("*"));
        NetworkContext context = mock(NetworkContext.class);
        when(context.getSetting()).thenReturn(bootstrapSetting);
        when(context.getRpcMonitor()).thenReturn(monitor);
        return new EmbeddedChannel(new NettyMessageHandler(context));
    }

    private NetMessage modeMessage(MessageMode mode) {
        NetMessage message = mock(NetMessage.class);
        when(message.getMode()).thenReturn(mode);
        return message;
    }

    @Test
    @DisplayName("PONG 报文不触发任何头清理调用")
    void pongNeverTouchedByHeaderCleanup() {
        EmbeddedChannel channel = channelWith(mock(RpcMonitor.class));
        NetMessage pong = modeMessage(MessageMode.PONG);

        channel.writeInbound(pong);

        verify(pong, never()).removeAllHeaders();
        verify(pong, never()).removeHeaders(any());
    }

    @Test
    @DisplayName("PING 报文同样零触碰")
    void pingNeverTouchedByHeaderCleanup() {
        EmbeddedChannel channel = channelWith(mock(RpcMonitor.class));
        NetMessage ping = modeMessage(MessageMode.PING);

        channel.writeInbound(ping);

        verify(ping, never()).removeAllHeaders();
    }

    @Test
    @DisplayName("兼容锚：普通消息的忽略头清理语义保持不变")
    void normalMessageStillCleaned() {
        EmbeddedChannel channel = channelWith(mock(RpcMonitor.class));
        NetMessage request = modeMessage(MessageMode.REQUEST);

        channel.writeInbound(request);

        verify(request, times(1)).removeAllHeaders();
    }

}
