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
package com.tny.game.common.lifecycle;

import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 生命周期注册表与优先级边界修复契约：
 * Lifecycle 内层表 HashMap→并发表 + value() check-then-act 竞态、
 * higher(highest) 溢出成假最高级、lower(lowest) 产出 0 级。
 */
class LifecycleRegistryFixTest {

    public static class StarterA implements AppPostStart {
        @Override
        public void postStart() {
        }
    }

    public static class StarterB implements AppPostStart {
        @Override
        public void postStart() {
        }
    }

    public static class StarterC implements AppPostStart {
        @Override
        public void postStart() {
        }
    }

    @Test
    void valueIsSingletonAcrossPriorityArguments() {
        PostStarter first = PostStarter.value(StarterA.class, LifecycleLevel.CUSTOM_LEVEL_1);
        PostStarter second = PostStarter.value(StarterA.class, LifecycleLevel.SYSTEM_LEVEL_1);
        assertSame(first, second, "重复 value 必须返回同一实例（优先级参数二次调用被忽略为现契约）");
        assertEquals(LifecycleLevel.CUSTOM_LEVEL_1.getOrder(), first.getOrder());
    }

    @Test
    void concurrentValueYieldsUniqueInstance() throws Exception {
        int threads = 16;
        CountDownLatch start = new CountDownLatch(1);
        AtomicReferenceArray<PostStarter> results = new AtomicReferenceArray<>(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            final int index = t;
            pool.execute(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                results.set(index, PostStarter.value(StarterB.class, LifecycleLevel.CUSTOM_LEVEL_3));
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        for (int t = 1; t < threads; t++) {
            assertSame(results.get(0), results.get(t), "并发首注册出现多实例或抛\"已经存在\"（A1 竞态）");
        }
    }

    @Test
    void appendEnforcesOrderDirection() {
        PrepareStarter high = PrepareStarter.value(StarterHigh.class, LifecyclePriorities.of(10));
        PrepareStarter lower = PrepareStarter.value(StarterLow.class, LifecyclePriorities.of(20));
        // 链要求后继 order ≤ 前驱（数值大=更晚执行）
        assertThrows(IllegalArgumentException.class, () -> high.append(lower));
        PrepareStarter earlier = PrepareStarter.value(StarterEarlier.class, LifecyclePriorities.of(5));
        assertSame(earlier, high.append(earlier));
        // next 已存在不得再 append
        assertThrows(IllegalArgumentException.class, () -> high.append(
                PrepareStarter.value(Another.class, LifecyclePriorities.of(2))), "next 已存在不得再 append");
        // 被 append 过（prev 已存在）的节点不得再挂到别的链前
        assertThrows(IllegalArgumentException.class, () ->
                PrepareStarter.value(PrepareC.class, LifecyclePriorities.of(3)).append(earlier),
                "prev is exist 校验必须生效");
    }

    public static class StarterHigh implements AppPrepareStart {
        @Override
        public void prepareStart() throws Exception {
        }
    }

    public static class StarterLow implements AppPrepareStart {
        @Override
        public void prepareStart() throws Exception {
        }
    }

    public static class StarterEarlier implements AppPrepareStart {
        @Override
        public void prepareStart() throws Exception {
        }
    }

    public static class PrepareC implements AppPrepareStart {
        @Override
        public void prepareStart() throws Exception {
        }
    }

    public static class Another implements AppPrepareStart {
        @Override
        public void prepareStart() throws Exception {
        }
    }

    // ---- LifecyclePriorities 边界 ----

    @Test
    void higherBeyondMaxThrowsInsteadOfOverflow() {
        // 原实现：MAX+1 溢出为 MIN_VALUE 且检查放行 → "最高优先级"实际变最后执行
        LifecyclePriority highest = LifecyclePriorities.highest();
        assertThrows(IllegalArgumentException.class, () -> LifecyclePriorities.higher(highest));
        assertThrows(IllegalArgumentException.class, () -> LifecyclePriorities.higher(highest, 5));
        // highest() == MAX 档位复用
        assertEquals(Integer.MAX_VALUE, highest.getOrder());
    }

    @Test
    void lowerBeyondMinThrows() {
        LifecyclePriority lowest = LifecyclePriorities.lowest();
        // 原实现 lower(lowest)=0 级放行（0 < lowest=1 语义矛盾）
        assertThrows(IllegalArgumentException.class, () -> LifecyclePriorities.lower(lowest));
        assertEquals(1, lowest.getOrder());
        assertEquals(2, LifecyclePriorities.lower(LifecyclePriorities.of(3)).getOrder());
    }

    @Test
    void ofReusesEnumInstancesForKnownLevels() {
        assertSame(LifecycleLevel.SYSTEM_LEVEL_1, LifecyclePriorities.of(LifecycleLevel.SYSTEM_LEVEL_1.getOrder()));
    }

}
