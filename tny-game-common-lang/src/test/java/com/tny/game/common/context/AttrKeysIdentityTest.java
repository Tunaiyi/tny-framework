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
package com.tny.game.common.context;

import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AttrKeys 键身份契约（回归：loadOrCreate 查询用短名、写入用全名——
 * 命名空间劫持、同 name 双实例导致 attributes2Map 撞 IllegalStateException、
 * key(Class,String) 每次调用 cache-miss 反复 new）。
 * 生产形态证据：NetAttrKeys 用 key(String)，MessageSequenceCheckerPlugin/AutoManageAdvice 用 key(Class,String)。
 */
class AttrKeysIdentityTest {

    private static final AtomicLong COUNTER = new AtomicLong();

    @Test
    void sameNamespacedKeyReturnsSameInstance() {
        AttrKey<Object> k1 = AttrKeys.key(AttrKeysIdentityTest.class, "seq");
        AttrKey<Object> k2 = AttrKeys.key(AttrKeysIdentityTest.class, "seq");
        assertSame(k1, k2, "同 (类,名) 必须恒等（原实现每次 cache-miss 新建实例）");
        assertEquals("seq", k1.name());
    }

    @Test
    void differentNamespacesAreSeparateKeys() {
        String name = "isolated-" + COUNTER.incrementAndGet();
        AttrKey<Object> a = AttrKeys.key(String.class, name);
        AttrKey<Object> b = AttrKeys.key(Integer.class, name);
        assertNotSame(a, b, "不同命名空间不得互相命中");
        // 全局短名键不与类命名空间键互相劫持
        AttrKey<Object> global = AttrKeys.key("global-" + name);
        assertNotSame(global, a);
    }

    /** 顺序不敏感：先建类键再建同名全局键，两者互不可见 */
    @Test
    void creationOrderDoesNotFuseNamespaces() {
        String name = "order-" + COUNTER.incrementAndGet();
        AttrKey<Object> namespaced = AttrKeys.key(Object.class, name);
        AttrKey<Object> global = AttrKeys.key(name);
        assertNotSame(namespaced, global, "全局键不得劫持命名空间键（原实现 get(短名) 命中）");
        assertNotSame(AttrKeys.key(Object.class, name), global);
    }

    /** 并发首次创建恒等 */
    @Test
    void concurrentCreationIsUnique() throws Exception {
        String name = "race-" + COUNTER.incrementAndGet();
        int threads = 16;
        CountDownLatch start = new CountDownLatch(1);
        AtomicReferenceArray<AttrKey<Object>> results = new AtomicReferenceArray<>(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            final int index = t;
            pool.execute(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                results.set(index, AttrKeys.key(AttrKeysIdentityTest.class, name));
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        for (int t = 1; t < threads; t++) {
            assertSame(results.get(0), results.get(t), "并发创建出现双实例");
        }
    }

    /** attributes 键失配回归：同 (类,名) 的键 set/get 必须闭环 */
    @Test
    void attributeRoundTripWithNamespacedKey() {
        AttrKey<String> key = AttrKeys.key(AttrKeysIdentityTest.class, "roundtrip");
        Attributes attributes = ContextAttributes.create();
        attributes.setAttribute(key, "v1");
        assertEquals("v1", attributes.getAttribute(key));
        assertEquals("v1", attributes.getAttribute(AttrKeys.key(AttrKeysIdentityTest.class, "roundtrip")));
    }

    /** 同 name 不同命名空间的两个键共存时 attributes2Map 的语义钉桩（name 冲突需明确策略——现记录） */
    @Test
    void attributes2MapWithDistinctNamespaceSameName() {
        String name = "dup-" + COUNTER.incrementAndGet();
        AttrKey<String> k1 = AttrKeys.key(String.class, name);
        AttrKey<String> k2 = AttrKeys.key(Integer.class, name);
        Attributes attributes = ContextAttributes.create();
        attributes.setAttribute(k1, "a");
        // 两个同 name 键都放入时 toMap 撞 IllegalStateException（缺陷 #7 附带面，钉桩现状）
        assertThrows(IllegalStateException.class, () -> {
            Attributes both = ContextAttributes.create();
            both.setAttribute(k1, "a");
            both.setAttribute(k2, "b");
            AttrKeys.attributes2Map(both);
        }, "同 name 双键在 attributes2Map 必须显式冲突而非静默覆盖");
        // 单键场景正常
        assertEquals("a", AttrKeys.attributes2Map(attributes).get(name));
    }

}
