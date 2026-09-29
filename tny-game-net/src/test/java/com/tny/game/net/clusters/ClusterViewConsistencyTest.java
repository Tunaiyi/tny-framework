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
package com.tny.game.net.clusters;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.allot.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 25（Wave-C）红灯基线：实例注册幂等与健康翻转视图时效（relay-cluster-view 契约）。
 */
class ClusterViewConsistencyTest {

    private static class TestCluster extends BaseRemoteServeCluster {

        TestCluster() {
            super("svc-a", "service-a", "u", mock(ServeInstanceAllotStrategy.class), mock(RelayLinkAllotStrategy.class));
        }

        @Override
        public com.tny.game.net.clusters.RemoteServeClusterContext getContext() {
            return null;
        }
    }

    private static NetRelayServeInstance instance(long id, AtomicBoolean healthy) {
        NetRelayServeInstance instance = mock(NetRelayServeInstance.class);
        when(instance.getId()).thenReturn(id);
        when(instance.isHealthy()).thenAnswer(invocation -> healthy.get());
        when(instance.isClose()).thenReturn(false);
        return instance;
    }

    @Test
    @DisplayName("同标识重复注册返回既有实例（幂等，供上层拦截孤儿连接器）")
    void duplicateRegistrationReturnsExisting() {
        TestCluster cluster = new TestCluster();
        AtomicBoolean healthy = new AtomicBoolean(true);
        NetRelayServeInstance first = instance(5L, healthy);
        NetRelayServeInstance second = instance(5L, healthy);

        assertSame(first, cluster.registerInstance(first));
        assertSame(first, cluster.registerInstance(second),
                "冲突必须返回既有实例（修复前恒返回新实例，调用方无从拦截）");
    }

    @Test
    @DisplayName("健康翻转在下一刷新周期反映到分配视图")
    void healthFlipPropagatesToHealthyView() {
        TestCluster cluster = new TestCluster();
        AtomicBoolean healthy = new AtomicBoolean(true);
        NetRelayServeInstance inst = instance(6L, healthy);
        cluster.registerInstance(inst);
        assertEquals(1, cluster.getHealthyLocalInstances().size(), "前置：健康实例在分配视图内");

        healthy.set(false);
        cluster.refreshInstances();

        assertTrue(cluster.getHealthyLocalInstances().isEmpty(), "健康翻转后不得再被分配选中（healthy 字段缺 volatile 时的可见性面）");
    }

}
