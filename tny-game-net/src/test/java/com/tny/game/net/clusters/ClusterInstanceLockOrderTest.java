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
package com.tny.game.net.clusters;


import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.concurrent.locks.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 16（Wave-C）红灯基线：实例锁与链路锁不得 ABBA——
 * 不变量断言"集群级刷新与旧链路终结不得在 linkLock 临界区内触达"
 * （修复前 register/relieve 持 linkLock 调 cluster.refreshInstances(instanceLock)，
 * 与 unregisterInstance 的 instanceLock→instance.close()(linkLock) 反向成环）。
 */
class ClusterInstanceLockOrderTest {

    private final AtomicHolder holder = new AtomicHolder();

    /** 探针服务类型：构造即注册，供实例构造器 checkService 解析 */
    enum ProbeServiceType implements com.tny.game.net.application.RpcServiceType {
        PROBE(991, "probe-service");
        private final int id;
        private final String service;

        ProbeServiceType(int id, String service) {
            this.id = id;
            this.service = service;
            this.register();
        }

        @Override
        public int id() {
            return id;
        }

        @Override
        public String getGroup() {
            return service;
        }

        @Override
        public String getService() {
            return service;
        }
    }

    private NetRemoteServeCluster clusterMock() {
        ProbeServiceType.PROBE.getService(); // 触发枚举初始化即注册
        NetRemoteServeCluster cluster = mock(NetRemoteServeCluster.class);
        when(cluster.getService()).thenReturn("probe-service");
        return cluster;
    }

    private NetRemoteServeCluster mockCluster(BaseRelayServeInstance probeTarget) throws Exception {
        NetRemoteServeCluster cluster = mock(NetRemoteServeCluster.class);
        doAnswer(invocation -> {
            holder.set("refresh", lock(probeTarget).isHeldByCurrentThread());
            return null;
        }).when(cluster).refreshInstances();
        return cluster;
    }

    private ReentrantLock lock(BaseRelayServeInstance instance) throws Exception {
        Field field = BaseRelayServeInstance.class.getDeclaredField("linkLock");
        field.setAccessible(true);
        return (ReentrantLock) field.get(instance);
    }

    private ClientRelayLink link(String id) {
        ClientRelayLink link = mock(ClientRelayLink.class);
        when(link.getId()).thenReturn(id);
        when(link.isActive()).thenReturn(true);
        return link;
    }

    @Test
    @DisplayName("register：集群刷新与旧链路关闭均在 linkLock 之外")
    void registerTouchesClusterOutsideLinkLock() throws Exception {
        NetRemoteServeCluster cluster = clusterMock();
        BaseRelayServeInstance instance = new BaseRelayServeInstance(cluster, mock(ServeNode.class));
        doAnswer(invocation -> {
            holder.set("refresh", lock(instance).isHeldByCurrentThread());
            return null;
        }).when(cluster).refreshInstances();

        ClientRelayLink first = link("l1");
        doAnswer(invocation -> {
            holder.set("close", lock(instance).isHeldByCurrentThread());
            return null;
        }).when(first).close();
        ClientRelayLink second = link("l1");

        instance.register(first);
        instance.register(second);   // 同 id 置换 → 旧链路 close + 集群刷新

        assertTrue(holder.called("refresh"), "集群刷新必须被触达");
        assertFalse(holder.get("refresh"), "refreshInstances 于 linkLock 内被调用（ABBA 前提，应红）");
        assertTrue(holder.called("close"));
        assertFalse(holder.get("close"), "被置换旧链路的 close 于 linkLock 内执行（应红）");
    }

    @Test
    @DisplayName("relieve：集群刷新在 linkLock 之外")
    void relieveRefreshesClusterOutsideLinkLock() throws Exception {
        NetRemoteServeCluster cluster = clusterMock();
        BaseRelayServeInstance instance = new BaseRelayServeInstance(cluster, mock(ServeNode.class));
        ClientRelayLink link = link("l9");
        instance.register(link);
        doAnswer(invocation -> {
            holder.set("relieve", lock(instance).isHeldByCurrentThread());
            return null;
        }).when(cluster).refreshInstances();

        instance.relieve(link);

        assertTrue(holder.called("relieve"));
        assertFalse(holder.get("relieve"), "relieve 路径 refreshInstances 持 linkLock（应红）");
    }

    static final class AtomicHolder {

        private final java.util.Map<String, Boolean> firsts = new java.util.concurrent.ConcurrentHashMap<>();

        void set(String key, boolean held) {
            firsts.putIfAbsent(key, held);
        }

        boolean called(String key) {
            return firsts.containsKey(key);
        }

        boolean get(String key) {
            return firsts.get(key);
        }
    }

}
