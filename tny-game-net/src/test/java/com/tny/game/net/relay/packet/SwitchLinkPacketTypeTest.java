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
package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.arguments.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 11（Wave-B）红灯基线：链路切换包必须携带"切换"类型、服务端按迁移语义处理且会话保留
 * （relay-link"链路切换按隧道迁移语义处理"契约）。
 * 修复前三参构造器（工厂唯一入口）硬编码 TUNNEL_CONNECT——线上所有切换包伪装成连接包，
 * 接收端走"新建隧道连接"分支，销毁既有业务会话。
 */
class SwitchLinkPacketTypeTest {

    @Test
    @DisplayName("工厂产出的切换包类型字段必须为 TUNNEL_SWITCH_LINK")
    void factoryProducedPacketCarriesSwitchType() {
        TunnelVoidArguments arguments = new TunnelVoidArguments(11L, 22L);
        RelayPacket<RelayPacketArguments> packet = RelayPacketType.TUNNEL_SWITCH_LINK.createPacket(3, arguments, System.currentTimeMillis());
        assertEquals(RelayPacketType.TUNNEL_SWITCH_LINK, packet.getType(),
                "修复前为 TUNNEL_CONNECT：切换通知在线上被解释为隧道建立");
    }

    @Test
    @DisplayName("迁移处理：既有隧道换绑新链路，会话保留、不关闭不断开")
    void switchKeepsTunnelAndSession() throws Exception {
        DefaultServerRelayExplorer explorer = new DefaultServerRelayExplorer();
        ServerRelayTunnel tunnel = mock(ServerRelayTunnel.class);
        when(tunnel.getInstanceId()).thenReturn(11L);
        when(tunnel.getId()).thenReturn(22L);
        explorer.putTunnel(tunnel);

        ServerRelayLink registeredLink = mock(ServerRelayLink.class);
        Field linkMapField = DefaultServerRelayExplorer.class.getDeclaredField("linkMap");
        linkMapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, ServerRelayLink> linkMap = (Map<String, ServerRelayLink>) linkMapField.get(explorer);
        linkMap.put("new-link", registeredLink);

        NetRelayLink link = mock(NetRelayLink.class);
        when(link.getId()).thenReturn("new-link");
        when(tunnel.switchLink(registeredLink)).thenReturn(true);

        explorer.switchTunnelLink(link, 11L, 22L);

        verify(tunnel).switchLink(same(registeredLink));
        verify(tunnel, never()).close();
        verify(tunnel, never()).disconnect();
        verify(link, never()).write(any(RelayPacketFactory.class), any(RelayPacketArguments.class));
    }

    @Test
    @DisplayName("兼容锚：隧道连接包类型经工厂仍为 TUNNEL_CONNECT（旧语义保留）")
    void connectPacketTypeUnchanged() {
        TunnelConnectArguments arguments = mock(TunnelConnectArguments.class);
        RelayPacket<RelayPacketArguments> packet = RelayPacketType.TUNNEL_CONNECT.createPacket(5, arguments, System.currentTimeMillis());
        assertEquals(RelayPacketType.TUNNEL_CONNECT, packet.getType());
    }

}
