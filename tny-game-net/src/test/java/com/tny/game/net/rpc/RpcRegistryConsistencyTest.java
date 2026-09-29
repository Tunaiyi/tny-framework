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
package com.tny.game.net.rpc;

import com.tny.game.net.application.*;
import com.tny.game.net.session.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 组 22（Wave-C）红灯基线：RPC 注册表发布原子性、节点收缩、活性跃迁与接入绑定安全
 * （net-rpc-registry 全部 Requirement）。
 */
class RpcRegistryConsistencyTest {

    enum ProbeRpcType implements RpcServiceType {
        RPC_PROBE(992, "probe-rpc-service");
        private final int id;
        private final String service;

        ProbeRpcType(int id, String service) {
            this.id = id;
            this.service = service;
            this.register();
        }

        @Override
        public int id() {
            return id;
        }

        @Override
        public String getService() {
            return service;
        }
    }

    private static Session sessionOf(int serverId, long accessId) {
        RpcAccessIdentify identify = new RpcAccessIdentify(ProbeRpcType.RPC_PROBE, serverId, (int) (accessId % 10000));
        Session session = mock(Session.class);
        when(session.getIdentifyToken(RpcAccessIdentify.class)).thenReturn(identify);
        when(session.identifyToken(RpcAccessIdentify.class)).thenReturn(java.util.Optional.of(identify));
        when(session.getIdentify()).thenReturn(accessId);
        when(session.attributes()).thenReturn(new com.tny.game.common.context.AbstractAttributes() {});
        return session;
    }

    private static class CountingNodeSet extends RpcServiceNodeSet {

        final AtomicInteger activates = new AtomicInteger();
        final AtomicInteger unactivates = new AtomicInteger();

        CountingNodeSet() {
            super(ProbeRpcType.RPC_PROBE);
        }

        @Override
        protected void onNodeActivate(RpcServiceNode rpcNode) {
            activates.incrementAndGet();
            super.onNodeActivate(rpcNode);
        }

        @Override
        protected void onNodeUnactivated(RpcServiceNode rpcNode) {
            unactivates.incrementAndGet();
            super.onNodeUnactivated(rpcNode);
        }
    }

    @Test
    @DisplayName("节点激活/失活跃迁各恰好一次（修复前反向：首激活不通知、失活不可达）")
    void nodeTransitionsFireOnceEach() {
        CountingNodeSet nodeSet = new CountingNodeSet();
        Session s1 = sessionOf(7, 101L);
        Session s2 = sessionOf(7, 102L);

        nodeSet.addSession(s1);
        assertEquals(1, nodeSet.activates.get(), "首个接入必须通知激活");
        nodeSet.addSession(s2);
        assertEquals(1, nodeSet.activates.get(), "非跃迁加入不得重复通知");

        nodeSet.removeSession(s1);
        assertEquals(0, nodeSet.unactivates.get(), "仍有存活接入不得通知失活");
        nodeSet.removeSession(s2);
        assertEquals(1, nodeSet.unactivates.get(), "归零必须恰好一次通知失活（修复前不可达）");
    }

    @Test
    @DisplayName("接入归零后节点从注册表收缩消失")
    void emptiedNodeIsEvicted() {
        RpcServiceNodeSet nodeSet = new RpcServiceNodeSet(ProbeRpcType.RPC_PROBE);
        Session s1 = sessionOf(8, 201L);
        nodeSet.addSession(s1);
        assertNotNull(nodeSet.findInvokeNode(8));

        nodeSet.removeSession(s1);

        assertNull(nodeSet.findInvokeNode(8), "接入归零的节点必须摘除（修复前僵尸驻留且视图永不收缩）");
        assertTrue(nodeSet.getOrderInvokeNodes().isEmpty());
    }

    @Test
    @DisplayName("陌生节点移除事件与缺 token 事件均安全忽略")
    void unknownNodeRemovalIsInert() {
        RpcServiceNodeSet nodeSet = new RpcServiceNodeSet(ProbeRpcType.RPC_PROBE);

        assertDoesNotThrow(() -> nodeSet.removeSession(sessionOf(9, 301L)));
        assertNull(nodeSet.findInvokeNode(9), "移除事件不得为不存在节点新建空壳");

        Session tokenless = mock(Session.class);
        when(tokenless.identifyToken(RpcAccessIdentify.class)).thenReturn(java.util.Optional.empty());
        assertDoesNotThrow(() -> nodeSet.removeSession(tokenless));
    }

    @Test
    @DisplayName("并发增删后视图与存活集合一致（发布原子性压力）")
    void concurrentMutationsConverge() throws Exception {
        RpcServiceNodeSet nodeSet = new RpcServiceNodeSet(ProbeRpcType.RPC_PROBE);
        int sessions = 24;
        java.util.List<Session> alive = new java.util.ArrayList<>();
        for (int i = 0; i < sessions; i++) {
            Session s = sessionOf(100 + i, 1000L + i);
            alive.add(s);
            nodeSet.addSession(s);
        }
        java.util.concurrent.CyclicBarrier barrier = new java.util.concurrent.CyclicBarrier(sessions);
        Thread[] threads = new Thread[sessions];
        for (int i = 0; i < sessions; i++) {
            Session s = alive.get(i);
            threads[i] = new Thread(() -> {
                try {
                    barrier.await(5, java.util.concurrent.TimeUnit.SECONDS);
                } catch (Exception ignored) {
                }
                nodeSet.removeSession(s);
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join(10000);
        }

        assertTrue(nodeSet.getOrderInvokeNodes().isEmpty(), "并发摘除完成后视图必须为空（last-writer-wins 竞态会复活节点）");
    }

    // ==================== ContactNodeSet ====================

    @Test
    @DisplayName("未绑定查询为空、活性按真实绑定、重复绑定先到者保留")
    void contactNodeSetBindingSafety() {
        ContactType contactType = DefaultContactType.DEFAULT_USER;
        ContactNodeSet nodeSet = new ContactNodeSet(contactType);

        assertFalse(nodeSet.isActive(), "未绑定不得报告活跃（修复前恒 true）");
        assertDoesNotThrow(() -> assertNull(nodeSet.getAccess(1L)), "未绑定查询必须返回空而非内部异常（修复前 NPE）");

        SessionKeeper first = mock(SessionKeeper.class);
        Session session = sessionOf(1, 555L);
        when(first.getSession(555L)).thenReturn(session);
        nodeSet.bind(first);
        assertTrue(nodeSet.isActive());
        assertNotNull(nodeSet.getAccess(555L));

        nodeSet.bind(mock(SessionKeeper.class)); // 重复绑定：保留先到者并告警
        assertNotNull(nodeSet.getAccess(555L), "后到绑定不得覆盖先到者");
    }

}
