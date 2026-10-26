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
package com.tny.game.net.relay.link.allot;

import com.tny.game.net.clusters.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 14（Wave-B）红灯基线：链路/实例分配策略在 size>1 时下标恒合法
 * （修复前 Random 用全值域 nextInt()%size 约半数取负抛 IndexOutOfBounds；
 * Polling 计数器 int 溢出后同病）。
 */
class RelayAllotStrategyTest {

    private static final List<ClientRelayLink> LINKS = List.of(mock(ClientRelayLink.class), mock(ClientRelayLink.class), mock(ClientRelayLink.class));

    @Test
    @DisplayName("随机策略 1000 次分配恒返回合法元素（修复前必然抛错）")
    void randomNeverProducesNegativeIndex() {
        RandomRelayAllotStrategy strategy = new RandomRelayAllotStrategy();
        RelayServeInstance instance = mock(RelayServeInstance.class);
        when(instance.getActiveRelayLinks()).thenReturn(LINKS);
        Tunnel tunnel = mock(Tunnel.class);
        for (int i = 0; i < 1000; i++) {
            assertNotNull(strategy.allot(tunnel, instance), "第 " + i + " 次分配失败即链路建立随机中断");
        }
    }

    @Test
    @DisplayName("轮询策略计数器溢出后仍合法")
    void pollingSurvivesCounterOverflow() throws Exception {
        PollingRelayAllotStrategy strategy = new PollingRelayAllotStrategy();
        Field field = PollingRelayAllotStrategy.class.getDeclaredField("linkCounter");
        field.setAccessible(true);
        ((AtomicInteger) field.get(strategy)).set(Integer.MAX_VALUE - 1);
        RelayServeInstance instance = mock(RelayServeInstance.class);
        when(instance.getActiveRelayLinks()).thenReturn(LINKS);
        Tunnel tunnel = mock(Tunnel.class);
        assertNotNull(strategy.allot(tunnel, instance));
        assertNotNull(strategy.allot(tunnel, instance), "溢出转负后取模为负下标（修复前抛 IndexOutOfBounds）");
    }

    @Test
    @DisplayName("兼容锚：空集合返回 null、单元素直返")
    void edgeSizesUnchanged() {
        RandomRelayAllotStrategy strategy = new RandomRelayAllotStrategy();
        RelayServeInstance empty = mock(RelayServeInstance.class);
        when(empty.getActiveRelayLinks()).thenReturn(List.of());
        assertNull(strategy.allot(mock(Tunnel.class), empty));
        ClientRelayLink only = mock(ClientRelayLink.class);
        when(empty.getActiveRelayLinks()).thenReturn(List.of(only));
        assertSame(only, strategy.allot(mock(Tunnel.class), empty));
    }

}
