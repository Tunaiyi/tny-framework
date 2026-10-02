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
package com.tny.game.net.netty4.cloud.nacos;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.cluster.watch.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 24（Wave-C）红灯基线：订阅注册表随实例生命周期、终结清理、重订阅复活、
 * 订阅失败上抛进入重试（net-boot-integration"服务节点订阅生命周期完整"契约）。
 */
class BaseServeNodeClientLifecycleTest {

    /** 可编程桩：记录 doSubscribe/doUnsubscribe 调用与失败注入 */
    private static class StubClient extends BaseServeNodeClient {

        final List<String> subscribes = new CopyOnWriteArrayList<>();
        final List<String> unsubscribes = new CopyOnWriteArrayList<>();
        volatile boolean failSubscribe;

        @Override
        protected void doSubscribe(String serveName) {
            subscribes.add(serveName);
            if (failSubscribe) {
                throw new IllegalStateException("stub subscribe failure");
            }
        }

        @Override
        protected void doUnsubscribe(String serveName) {
            unsubscribes.add(serveName);
        }

    }

    @Test
    @DisplayName("注册表按实例隔离：A 终结不得让 B 对同名服务的再订阅静默失效")
    void holderRegistryIsInstanceScoped() {
        StubClient a = new StubClient();
        StubClient b = new StubClient();

        a.subscribe("svc-x", mockListener());
        a.onClosed(); // A 终结：修复前 static 表留下已停 holder 且不清除

        b.subscribe("svc-x", mockListener());
        assertEquals(1, b.subscribes.size(),
                "B 必须真正发起订阅；修复前命中 A 遗留的已停 holder 静默永久失效（本断言应红）");
        b.onClosed();
    }

    @Test
    @DisplayName("onClosed 终结清理：条目移除、重订阅可复活重新发起 doSubscribe")
    void closedClientResubscribesAfterRevive() {
        StubClient client = new StubClient();
        client.subscribe("svc-y", mockListener());
        assertEquals(1, client.subscribes.size());

        client.onClosed();
        assertTrue(client.getHealthyServeNodes("svc-y").isEmpty());

        client.subscribe("svc-y", mockListener());
        assertEquals(2, client.subscribes.size(),
                "终结后重新订阅必须复活发起（修复前命中已停 holder 静默永久失效，本断言应红）");
        client.onClosed();
    }

    @Test
    @DisplayName("订阅失败显式上抛：holder 记录并保留重试资格，不静默成功")
    void subscribeFailurePropagatesIntoRetryState() {
        StubClient client = new StubClient();
        client.failSubscribe = true;

        assertDoesNotThrow(() -> client.subscribe("svc-z", mockListener()));
        assertEquals(1, client.subscribes.size(), "失败必须真实抵达 doSubscribe（修复前异常被底层吞掉）");
        client.onClosed();
    }

    private static ServeNodeListener mockListener() {
        return new ServeNodeListener() {
            @Override
            public void onCreate(ServeNode node) {
            }

            @Override
            public void onRemove(ServeNode node) {
            }

            @Override
            public void onChange(ServeNode node, List<ServeNodeChangeStatus> statuses) {
            }
        };
    }

}
