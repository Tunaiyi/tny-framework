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
package com.tny.game.net.relay.link;

import com.tny.game.net.application.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.rpc.NetAccessMode;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 中继注册表生命周期（relay-link 规格 · 链路摘除/隧道显式关闭摘除/迁移保留）。
 */
class RelayExplorerRegistryTest {

    DefaultServerRelayExplorer explorer = new DefaultServerRelayExplorer();

    /** ① 链路物理断开后条目摘除（当前无摘除逻辑，应红） */
    @Test
    void disconnectedLinkIsUnregistered() throws Exception {
        RelayTransport transport = openLink("svc", 1L, "key-1");
        assertEquals(1, linkMap().size(), "前置：开通成功应入表");

        // 模拟物理断线路径：transport 死亡 → 链路进入断开态
        transport.close();
        linkOf(transport).disconnect();

        assertTrue(linkMap().isEmpty(), "断开链路的条目必须摘除（当前滞留，本断言应红）");
    }

    /** ② 链路主动关闭同样摘除；断开后重建同键链路不受残留干扰 */
    @Test
    void closedLinkIsUnregisteredAndRebuildIsClean() throws Exception {
        RelayTransport transport = openLink("svc", 2L, "key-2");
        linkOf(transport).close();
        assertTrue(linkMap().isEmpty(), "关闭链路条目必须摘除（当前应红）");

        openLink("svc", 2L, "key-2");
        assertEquals(1, linkMap().size(), "同键重建后注册表仅含新链路");
    }

    /** ③ re-CONNECT 迁移置换：同标识隧道重连时旧隧道被关闭恰好一次、新条目可查（现状应绿，防回归固化） */
    @Test
    void tunnelReconnectReplacesAndClosesOldOnce() {
        ServerRelayTunnel oldTunnel = tunnel(100L, 9L);
        ServerRelayTunnel newTunnel = tunnel(100L, 9L);
        explorer.putTunnel(oldTunnel);

        explorer.putTunnel(newTunnel);

        assertSame(newTunnel, explorer.getTunnel(100L, 9L), "置换后按标识查得新隧道");
        verify(oldTunnel, times(1)).disconnect();
    }

    /** ④ 迁移保留反例：链路断开不得清理其隧道条目（现状应绿——防未来过度清理的路障） */
    @Test
    void linkDisconnectDoesNotEvictTunnelEntries() throws Exception {
        RelayTransport transport = openLink("svc", 3L, "key-3");
        explorer.putTunnel(tunnel(3L, 77L));

        linkOf(transport).disconnect();

        assertNotNull(explorer.getTunnel(3L, 77L), "断链窗口的隧道条目必须保留（迁移前提）");
    }

    /** ⑤ 隧道协议关闭：先摘除后关闭（当前只关不摘，应红） */
    @Test
    void explicitTunnelCloseRemovesEntry() {
        ServerRelayTunnel tunnel = tunnel(50L, 8L);
        explorer.putTunnel(tunnel);

        explorer.closeTunnel(50L, 8L);

        assertNull(explorer.getTunnel(50L, 8L), "显式关闭后条目必须摘除（当前滞留，本断言应红）");
        verify(tunnel, times(1)).close();
    }

    /** ⑥ 重复关闭幂等：第二次为无害空操作（当前二次 get+close，应红） */
    @Test
    void repeatedTunnelCloseIsInert() {
        ServerRelayTunnel tunnel = tunnel(51L, 8L);
        explorer.putTunnel(tunnel);

        explorer.closeTunnel(51L, 8L);
        explorer.closeTunnel(51L, 8L);

        verify(tunnel, times(1)).close();
    }

    /** ⑦ 复合键作用域：同隧道 ID 不同实例的关闭互不影响（closeTunnel 摘除键完整性） */
    @Test
    void tunnelCloseIsScopedByCompositeKey() {
        explorer.putTunnel(tunnel(52L, 8L));
        ServerRelayTunnel sameIdOtherInstance = tunnel(53L, 8L);
        explorer.putTunnel(sameIdOtherInstance);

        explorer.closeTunnel(52L, 8L);

        assertNull(explorer.getTunnel(52L, 8L));
        assertSame(sameIdOtherInstance, explorer.getTunnel(53L, 8L), "不得误摘其他实例的同 ID 隧道");
    }

    /** ⑧ 并发关闭同一隧道：remove 唯一胜者——close 恰好执行一次（D2 幂等的压力版） */
    @Test
    void concurrentTunnelCloseHasSingleWinner() throws Exception {
        ServerRelayTunnel tunnel = tunnel(60L, 8L);
        explorer.putTunnel(tunnel);
        java.util.concurrent.CyclicBarrier barrier = new java.util.concurrent.CyclicBarrier(2);

        Runnable racer = () -> {
            try {
                barrier.await(5, java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception ignored) {
            }
            explorer.closeTunnel(60L, 8L);
        };
        Thread first = new Thread(racer, "close-racer-1");
        Thread second = new Thread(racer, "close-racer-2");
        first.start();
        second.start();
        first.join(5_000);
        second.join(5_000);

        verify(tunnel, times(1)).close();
        assertNull(explorer.getTunnel(60L, 8L));
    }

    /** ⑨ 断开摘除→同键重建→旧链路迟到的关闭事件：值 CAS 必须不摘新（规格场景一防护路径的实序构造） */
    @Test
    void staleLinkEventsNeverEvictRebuiltLink() throws Exception {
        RelayTransport firstTransport = openLink("svc", 4L, "key-4");
        NetRelayLink staleLink = linkOf(firstTransport);
        staleLink.disconnect();                       // 第一次摘除生效
        RelayTransport rebuiltTransport = openLink("svc", 4L, "key-4");
        NetRelayLink rebuiltLink = linkOf(rebuiltTransport);
        assertEquals(1, linkMap().size(), "同键重建应成功入表");

        staleLink.close();                            // 旧链路迟到事件：onDisconnect 再次触发摘除

        assertSame(rebuiltLink, linkMap().get(rebuiltLink.getId()), "旧链路的迟到事件不得误摘新链路（值相等 CAS）");
    }

    /** ⑩ 全生命周期序列：链路丢失（隧道保留）→ 隧道随后被显式关闭（正常摘除）——规格"迁移保留不拒绝显式清理" */
    @Test
    void retainedTunnelClosableAfterLinkLoss() throws Exception {
        RelayTransport transport = openLink("svc", 5L, "key-5");
        ServerRelayTunnel tunnel = tunnel(5L, 9L);
        explorer.putTunnel(tunnel);

        linkOf(transport).disconnect();               // 链路消亡，隧道保留
        assertNotNull(explorer.getTunnel(5L, 9L), "前置：保留窗口内条目在表");

        explorer.closeTunnel(5L, 9L);                 // 显式关闭仍可达
        assertNull(explorer.getTunnel(5L, 9L), "显式关闭必须正常摘除，不受保留语义阻挠");
        verify(tunnel, times(1)).close();
    }

    // ---------- 装配工具 ----------

    private RelayTransport openLink(String service, long instance, String key) {
        RelayTransport transport = mock(RelayTransport.class);
        when(transport.isActive()).thenReturn(true);
        explorer.acceptOpenLink(transport, mock(RpcServiceType.class), service, instance, key);
        return transport;
    }

    private NetRelayLink linkOf(RelayTransport transport) {
        return captureBoundLink(transport);
    }

    private static NetRelayLink captureBoundLink(RelayTransport transport) {
        // BaseRelayLink 构造内 transport.bind(this)：经 mock 的参数捕获取回链路
        var captor = org.mockito.ArgumentCaptor.forClass(NetRelayLink.class);
        verify(transport).bind(captor.capture());
        return captor.getValue();
    }

    private static ServerRelayTunnel tunnel(long instanceId, long tunnelId) {
        ServerRelayTunnel tunnel = mock(ServerRelayTunnel.class);
        when(tunnel.getInstanceId()).thenReturn(instanceId);
        when(tunnel.getId()).thenReturn(tunnelId);
        when(tunnel.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 7000));
        when(tunnel.getAccessMode()).thenReturn(NetAccessMode.SERVER);
        return tunnel;
    }

    @SuppressWarnings("unchecked")
    private Map<String, ServerRelayLink> linkMap() throws Exception {
        return (Map<String, ServerRelayLink>) field(explorer, DefaultServerRelayExplorer.class, "linkMap");
    }

    private static Object field(Object target, Class<?> owner, String name) throws Exception {
        Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }

}
