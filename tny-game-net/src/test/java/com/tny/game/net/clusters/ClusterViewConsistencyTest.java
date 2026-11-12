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
